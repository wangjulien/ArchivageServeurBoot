package com.telino.avp.repository;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.telino.avp.config.PropertySourceConfig;
import com.telino.avp.config.multids.AtomikosJtaConfig;

@Configuration
@ComponentScan(basePackages = { "com.telino.avp.dao" })
@Import({ PropertySourceConfig.class, AtomikosJtaConfig.class })
public class ConfigTestRepository {
	
	public static final String TEST_HASH = "This is test hash";
	public static final UUID TEST_LOG_ID = UUID.randomUUID();
	public static final UUID TEST_DOC_ID = UUID.randomUUID();
	public static final String DOC_TITILE = "This is doc title";
	
	
	@Bean
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
		return new TransactionTemplate(transactionManager);
	}
}
