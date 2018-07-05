package com.telino.avp.config.multids;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

@Configuration
@EnableTransactionManagement
@Import({ AtomikosMasterDataSourceConfig.class, AtomikosMirrorDataSourceConfig.class, ParamDataSourceConfig.class })
public class AtomikosJtaConfig {
	
	@Value("${hibernate.show_sql}")
    private boolean showSql;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		// JPA Java Persistence API with Hibernate Implementation of JPA is used
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

		// JPA vendor adapter will set SQL syntax to Postgres
		jpaVendorAdapter.setDatabase(Database.POSTGRESQL);

		// setting this will show SQLS on logs
		jpaVendorAdapter.setShowSql(showSql);

		return jpaVendorAdapter;
	}

	@Bean(name = "atomikosUserTransaction")
	public UserTransaction atomikosUserTransaction() throws SystemException {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		return userTransactionImp;
	}

	@Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
	public TransactionManager atomikosTransactionManager() {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(true);
		return userTransactionManager;
	}

	@Bean(name = "transactionManager")
	@DependsOn({ "atomikosUserTransaction", "atomikosTransactionManager" })
	public PlatformTransactionManager transactionManager() throws SystemException {
		UserTransaction atomikosUserTransaction = atomikosUserTransaction();
		TransactionManager atomikosTransactionManager = atomikosTransactionManager();

		AtomikosJtaPlatform.setJtaTransactionManager(atomikosUserTransaction, atomikosTransactionManager);
		return new JtaTransactionManager(atomikosUserTransaction, atomikosTransactionManager);
	}
}
