package com.telino.avp.config.multids;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

/**
 * Classe utile pour adapter Atomikos a Spring framework
 * 
 * @author jwang
 *
 */
public class AtomikosJtaPlatform extends AbstractJtaPlatform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4690974943322355592L;

	private static UserTransaction transaction;
	private static TransactionManager transactionManager;
	
	@Override
	protected UserTransaction locateUserTransaction() {
		return transaction;
	}

	@Override
	protected TransactionManager locateTransactionManager() {
		return transactionManager;
	}
	
	public static void setJtaTransactionManager(UserTransaction tx,TransactionManager txMger) {
		transaction = tx;
		transactionManager = txMger;
	}

}
