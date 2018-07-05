package com.telino.avp.config.multids;

import org.springframework.util.Assert;

/**
 * Dynamic DS contextholder, pour determiner le DS courant du thread
 * 
 * @author jwang
 *
 */
public class MasterDsContextHolder {
	
	private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

	public static void setCurrentDsId(final String dsId) {
		Assert.notNull(dsId, "Database type cannot be null");
		CONTEXT.set(dsId);
	}

	public static String getCurrentDsId() {
		return CONTEXT.get();
	}

	public static void clearDsId() {
		CONTEXT.remove();
	}
}
