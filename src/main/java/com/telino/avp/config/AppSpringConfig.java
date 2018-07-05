package com.telino.avp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.telino.avp.config.multids.AtomikosJtaConfig;
import com.telino.avp.service.storage.FSProc;
import com.telino.avp.service.storage.FSProcRemote;

/**
 * Point d'entree des fichiers de configuration de l'app
 * 
 * @author jwang
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.telino.avp" })
@EnableScheduling
@EnableAsync
@Import({ PropertySourceConfig.class, AtomikosJtaConfig.class, ScheduleConfig.class, AsyncConfig.class})
public class AppSpringConfig {

	public static final Integer APP_PARAM_ID = 1;

	@Bean
	public FSProc fsprocMaster() {
		return new FSProcRemote();
	}

	@Bean
	public FSProc fsprocMirror() {
		return new FSProcRemote();
	}
	
	@Bean
	public TransactionTemplate TransactionTemplate(PlatformTransactionManager transactionManager) {
		return new TransactionTemplate(transactionManager);
	}
}
