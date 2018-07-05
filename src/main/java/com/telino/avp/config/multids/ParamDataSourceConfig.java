package com.telino.avp.config.multids;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.atomikos.jdbc.AtomikosDataSourceBean;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(basePackages = {
		"com.telino.avp.dao.paramdao" }, entityManagerFactoryRef = "paramEntityManagerFactory", transactionManagerRef = "transactionManager")
public class ParamDataSourceConfig {

	@Value("${spring.paramds.id}")
    private String paramDsId;
	
	@Value("${spring.paramds.driverClassName}")
    private String paramDsDriver;
	
	@Value("${spring.paramds.username}")
    private String paramDsUser;
	
	@Value("${spring.paramds.password}")
    private String paramDsPsw;
	
	@Value("${spring.paramds.host}")
    private String paramDsHost;
	
	@Value("${spring.paramds.port}")
    private String paramDsPort;
	
	@Value("${spring.paramds.db}")
    private String paramDsDb;
	
	@Value("${spring.paramds.pool}")
    private int paramDsPool;
	
	@Value("${hibernate.hbm2ddl.auto}")
	private String hbm2ddlAuto;

	@Autowired
	private JpaVendorAdapter jpaVendorAdapter;

	@Bean
	public DataSource paramDataSource() {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName(paramDsId);
		ds.setXaDataSourceClassName(paramDsDriver);
		Properties p = new Properties();
		p.setProperty("user", paramDsUser);
		p.setProperty("password", paramDsPsw);
		p.setProperty("serverName", paramDsHost);
		p.setProperty("portNumber", paramDsPort);
		p.setProperty("databaseName", paramDsDb);
		ds.setXaProperties(p);
		ds.setPoolSize(paramDsPool);
		return ds;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean paramEntityManagerFactory() {
		// Entity Manager Factory configuration is required in order to manage entities
		// (tables) in Spring MVC
		// Entity Manager Factory will manage the life cycle of entities in Java
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

		// Entity manager factory requires a data source in order to store/list entities
		entityManagerFactory.setJtaDataSource(paramDataSource());

		// Java Config Dependency injection is provided here by setting JPA Vendor
		// Adapter (Hibernate)
		entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);

		// Entity Manager Factory will scan packages in order to find entities (@Entity)
		entityManagerFactory.setPackagesToScan("com.telino.avp.entitysyst");

		// Custom properties can be set using Properties
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", hbm2ddlAuto);
		jpaProperties.setProperty("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
		jpaProperties.setProperty("javax.persistence.transactionType", "JTA");

		entityManagerFactory.setJpaProperties(jpaProperties);

		return entityManagerFactory;

	}
}
