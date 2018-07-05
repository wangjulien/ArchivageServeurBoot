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
		"com.telino.avp.dao.masterdao" }, entityManagerFactoryRef = "masterEntityManagerFactory", transactionManagerRef = "transactionManager")
public class AtomikosMasterDataSourceConfig {

	@Value("${spring.masterds.id}")
    private String masterDsId;
	
	@Value("${spring.masterds.driverClassName}")
    private String masterDsDriver;
	
	@Value("${spring.masterds.username}")
    private String masterDsUser;
	
	@Value("${spring.masterds.password}")
    private String masterDsPsw;
	
	@Value("${spring.masterds.host}")
    private String masterDsHost;
	
	@Value("${spring.masterds.port}")
    private String masterDsPort;
	
	@Value("${spring.masterds.db}")
    private String masterDsDb;
	
	@Value("${spring.masterds.pool}")
    private int masterDsPool;
	
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
	public DataSource masterDynamicDs() {
		Map<Object, Object> targetDataSources = new HashMap<>();
		
		// Load potential DS
		DataSource masterDataSourceOne = masterDataSourceOne();
//		DataSource masterDataSourceTwo = masterDataSourceTwo();
		targetDataSources.put(((AtomikosDataSourceBean)masterDataSourceOne).getUniqueResourceName(), masterDataSourceOne);
//		targetDataSources.put(((AtomikosDataSourceBean)masterDataSourceTwo).getUniqueResourceName(), masterDataSourceTwo);

		MasterDataSourceRouter avpDataSourceRouter = new MasterDataSourceRouter();
		avpDataSourceRouter.setTargetDataSources(targetDataSources);
		avpDataSourceRouter.setDefaultTargetDataSource(masterDataSourceOne);
		return avpDataSourceRouter;
	}

	@Bean
	public DataSource masterDataSourceOne() {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName(masterDsId);
		ds.setXaDataSourceClassName(masterDsDriver);
		Properties p = new Properties();
		p.setProperty("user", masterDsUser);
		p.setProperty("password", masterDsPsw);
		p.setProperty("serverName", masterDsHost);
		p.setProperty("portNumber", masterDsPort);
		p.setProperty("databaseName", masterDsDb);
		ds.setXaProperties(p);
		ds.setPoolSize(masterDsPool);
		return ds;
	}
	
//	@Bean
//	public DataSource masterDataSourceTwo() {
//		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
//		ds.setUniqueResourceName("AVP_JULY");
//		ds.setXaDataSourceClassName(environment.getProperty("spring.masterds.driverClassName"));
//		Properties p = new Properties();
//		p.setProperty("user", environment.getProperty("spring.masterds.username"));
//		p.setProperty("password", environment.getProperty("spring.masterds.password"));
//		p.setProperty("serverName", environment.getProperty("spring.masterds.host"));
//		p.setProperty("portNumber", environment.getProperty("spring.masterds.port"));
//		p.setProperty("databaseName", "avp_july");
//		ds.setXaProperties(p);
//		ds.setPoolSize(Integer.valueOf(environment.getProperty("spring.masterds.pool")));
//		return ds;
//	}


	@Bean
	public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory() {
		// Entity Manager Factory configuration is required in order to manage entities
		// (tables) in Spring MVC
		// Entity Manager Factory will manage the life cycle of entities in Java
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

		// Entity manager factory requires a data source in order to store/list entities
		entityManagerFactory.setJtaDataSource(masterDynamicDs());

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
