package com.telino.avp;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.telino.avp.config.multids.DataSourceConfig;
import com.telino.avp.config.multids.DataSourceConfig.DsConfigObject;

@SpringBootApplication(scanBasePackages = { "com.telino.avp", "CdmsProg.nfz42013" })
@EnableScheduling
@EnableAsync
@EnableWebMvc
public class ArchivageServeurBootApplication {

	public static ApplicationContext SPRING_CONTEXT;

	@Autowired
	private DataSourceConfig dataSourceConfig;

	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(ArchivageServeurBootApplication.class, args);
	}

	@Bean
	public TomcatServletWebServerFactory tomcatFactory() {
		// !!! Hacking Code for passing Spring bean for non controlled class (CdmsProg)
		ArchivageServeurBootApplication.SPRING_CONTEXT = context;

		return new TomcatServletWebServerFactory() {

			@Override
			protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
				tomcat.enableNaming();
				return new TomcatWebServer(tomcat, getPort() >= 0);
			}

			@Override
			protected void postProcessContext(Context context) {

				for (DsConfigObject dsConfigObject : dataSourceConfig.getDsConfigList()) {
					ContextResource resource = new ContextResource();
					resource.setName("jdbc/" + dsConfigObject.getId());
					resource.setAuth("Container");
					resource.setType(DataSource.class.getName());
					resource.setProperty("driverClassName", "org.postgresql.Driver");
					resource.setProperty("factory", "org.apache.tomcat.jdbc.pool.DataSourceFactory");
					resource.setProperty("url", "jdbc:postgresql://" + dsConfigObject.getHost() + ":"
							+ dsConfigObject.getPort() + "/" + dsConfigObject.getDb());
					resource.setProperty("username", dsConfigObject.getUsername());
					resource.setProperty("password", dsConfigObject.getPassword());
					resource.setProperty("removeAbandonedTimeout", "30");
					resource.setProperty("logAbandoned", "true");

					context.getNamingResources().addResource(resource);
				}
			}
		};
	}
}
