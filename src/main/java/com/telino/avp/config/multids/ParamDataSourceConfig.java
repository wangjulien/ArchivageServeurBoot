package com.telino.avp.config.multids;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.atomikos.jdbc.AtomikosDataSourceBean;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(basePackages = {
		"com.telino.avp.dao.paramdao" }, entityManagerFactoryRef = "paramEntityManagerFactory", transactionManagerRef = "transactionManager")
public class ParamDataSourceConfig {

	@Autowired
	private Environment environment;

	@Autowired
	private JpaVendorAdapter jpaVendorAdapter;

	@Bean
	public DataSource paramDataSource() {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName(environment.getProperty("spring.paramds.id"));
		ds.setXaDataSourceClassName(environment.getProperty("spring.paramds.driverClassName"));
		Properties p = new Properties();
		p.setProperty("user", environment.getProperty("spring.paramds.username"));
		p.setProperty("password", environment.getProperty("spring.paramds.password"));
		p.setProperty("serverName", environment.getProperty("spring.paramds.host"));
		p.setProperty("portNumber", environment.getProperty("spring.paramds.port"));
		p.setProperty("databaseName", environment.getProperty("spring.paramds.db"));
		ds.setXaProperties(p);
		ds.setPoolSize(Integer.valueOf(environment.getProperty("spring.paramds.pool")));
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
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.setProperty("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
		jpaProperties.setProperty("javax.persistence.transactionType", "JTA");

		entityManagerFactory.setJpaProperties(jpaProperties);

		return entityManagerFactory;

	}
}
