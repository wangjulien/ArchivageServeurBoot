package com.telino.avp.config.multids;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring")
public class DataSourceConfig {

	private List<DsConfigObject> dsConfigList = new ArrayList<>();

	public List<DsConfigObject> getDsConfigList() {
		return dsConfigList;
	}

	public void setDsConfigList(List<DsConfigObject> list) {
		this.dsConfigList = list;
	}

	public static class DsConfigObject {
		private String id;
		private String driverClassName;
		private String host;
		private String port;
		private String db;
		private String username;
		private String password;
		private int pool;

		public DsConfigObject() {
			super();
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDriverClassName() {
			return driverClassName;
		}

		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		public String getDb() {
			return db;
		}

		public void setDb(String db) {
			this.db = db;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getPool() {
			return pool;
		}

		public void setPool(int pool) {
			this.pool = pool;
		}
	}
}
