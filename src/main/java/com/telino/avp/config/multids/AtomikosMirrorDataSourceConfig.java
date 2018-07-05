package com.telino.avp.config.multids;

import java.util.HashMap;
import java.util.Map;
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
		"com.telino.avp.dao.mirrordao" }, entityManagerFactoryRef = "mirrorEntityManagerFactory", transactionManagerRef = "transactionManager")
public class AtomikosMirrorDataSourceConfig {

	@Value("${spring.mirrords.id}")
    private String mirrorDsId;
	
	@Value("${spring.mirrords.driverClassName}")
    private String mirrorDsDriver;
	
	@Value("${spring.mirrords.username}")
    private String mirrorDsUser;
	
	@Value("${spring.mirrords.password}")
    private String mirrorDsPsw;
	
	@Value("${spring.mirrords.host}")
    private String mirrorDsHost;
	
	@Value("${spring.mirrords.port}")
    private String mirrorDsPort;
	
	@Value("${spring.mirrords.db}")
    private String mirrorDsDb;
	
	@Value("${spring.mirrords.pool}")
    private int mirrorDsPool;
	
	@Value("${hibernate.hbm2ddl.auto}")
	private String hbm2ddlAuto;

	@Autowired
	private JpaVendorAdapter jpaVendorAdapter;
	
	/**
	 * Dynamic routing DataSource
	 * 
	 * @return
	 */
	@Bean
	public DataSource mirrorDynamicDs() {
		Map<Object, Object> targetDataSources = new HashMap<>();
		
		// Load potential DS
		DataSource mirrorDataSourceOne = mirrorDataSourceOne();
//		DataSource mirrorDataSourceTwo = mirrorDataSourceTwo();
		targetDataSources.put(((AtomikosDataSourceBean)mirrorDataSourceOne).getUniqueResourceName(), mirrorDataSourceOne);
//		targetDataSources.put(((AtomikosDataSourceBean)mirrorDataSourceTwo).getUniqueResourceName(), mirrorDataSourceTwo);

		MirrorDataSourceRouter avpDataSourceRouter = new MirrorDataSourceRouter();
		avpDataSourceRouter.setTargetDataSources(targetDataSources);
		avpDataSourceRouter.setDefaultTargetDataSource(mirrorDataSourceOne);
		return avpDataSourceRouter;
	}

	@Bean
	public DataSource mirrorDataSourceOne() {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName(mirrorDsId);
		ds.setXaDataSourceClassName(mirrorDsDriver);
		Properties p = new Properties();
		p.setProperty("user", mirrorDsUser);
		p.setProperty("password", mirrorDsPsw);
		p.setProperty("serverName", mirrorDsHost);
		p.setProperty("portNumber", mirrorDsPort);
		p.setProperty("databaseName", mirrorDsDb);
		ds.setXaProperties(p);
		ds.setPoolSize(mirrorDsPool);
		return ds;
	}
	
//	@Bean
//	public DataSource mirrorDataSourceTwo() {
//		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
//		ds.setUniqueResourceName("AVP_JULY_M");
//		ds.setXaDataSourceClassName(environment.getProperty("spring.mirrords.driverClassName"));
//		Properties p = new Properties();
//		p.setProperty("user", environment.getProperty("spring.mirrords.username"));
//		p.setProperty("password", environment.getProperty("spring.mirrords.password"));
//		p.setProperty("serverName", environment.getProperty("spring.mirrords.host"));
//		p.setProperty("portNumber", environment.getProperty("spring.mirrords.port"));
//		p.setProperty("databaseName", "avp_july_m");
//		ds.setXaProperties(p);
//		ds.setPoolSize(Integer.valueOf(environment.getProperty("spring.mirrords.pool")));
//		return ds;
//	}

	@Bean
	public LocalContainerEntityManagerFactoryBean mirrorEntityManagerFactory() {
		// Entity Manager Factory configuration is required in order to manage entities
		// Entity Manager Factory will manage the life cycle of entities in Java
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

		// Entity manager factory requires a data source in order to store/list entities
		entityManagerFactory.setJtaDataSource(mirrorDynamicDs());

		// Java Config Dependency injection is provided here by setting JPA Vendor
		// Adapter (Hibernate)
		entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);

		// Entity Manager Factory will scan packages in order to find entities (@Entity)
		entityManagerFactory.setPackagesToScan("com.telino.avp.entity");

		// Custom properties can be set using Properties
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", hbm2ddlAuto);
		jpaProperties.setProperty("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
		jpaProperties.setProperty("javax.persistence.transactionType", "JTA");

		entityManagerFactory.setJpaProperties(jpaProperties);

		return entityManagerFactory;
	}
}
