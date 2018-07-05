package com.telino.avp.config.multids;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Customized Dynamic DS router, afin de router le DS en runtime
 * 
 * @author jwang
 *
 */
public class MasterDataSourceRouter extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return MasterDsContextHolder.getCurrentDsId();
	}

}
