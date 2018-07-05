package com.telino.avp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.telino.avp.service.storage.FSProc;
import com.telino.avp.service.storage.FSProcRemote;

/**
 * Point d'entree des fichiers de configuration de l'app
 * 
 * @author jwang
 *
 */
@Configuration
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
