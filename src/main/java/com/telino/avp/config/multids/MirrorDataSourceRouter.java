package com.telino.avp.config.multids;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Customized Dynamic DS router, afin de router le DS en runtime
 * 
 * @author jwang
 *
 */
public class MirrorDataSourceRouter extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return MirrorDsContextHolder.getCurrentDsId();
	}

}
