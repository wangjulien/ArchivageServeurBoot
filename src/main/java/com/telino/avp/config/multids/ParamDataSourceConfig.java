package com.telino.avp.config.multids;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.telino.avp.config.multids.DataSourceConfig.DsConfigObject;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(basePackages = {
		"com.telino.avp.dao.paramdao" }, entityManagerFactoryRef = "paramEntityManagerFactory", transactionManagerRef = "transactionManager")
public class ParamDataSourceConfig {

	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String hbm2ddlAuto;

	@Autowired
	private DataSourceConfig dataSourceConfig;

	@Bean
	public DataSource paramDataSource() {
		// If AVPNAV datasource does not exist, application will not start
		DsConfigObject dsConfigObject = dataSourceConfig.getDsConfigList().stream()
				.filter(e -> e.getId().contains("AVPNAV")).findFirst().orElseThrow(RuntimeException::new);
		
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName(dsConfigObject.getId());
		ds.setXaDataSourceClassName(dsConfigObject.getDriverClassName());
		Properties p = new Properties();
		p.setProperty("user", dsConfigObject.getUsername());
		p.setProperty("password", dsConfigObject.getPassword());
		p.setProperty("serverName", dsConfigObject.getHost());
		p.setProperty("portNumber", dsConfigObject.getPort());
		p.setProperty("databaseName", dsConfigObject.getDb());
		ds.setXaProperties(p);
		ds.setPoolSize(dsConfigObject.getPool());
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
		entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

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
