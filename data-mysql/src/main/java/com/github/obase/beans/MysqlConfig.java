package com.github.obase.beans;

public class MysqlConfig {
	public String key;
	public String address;
	public String database;
	public String username;
	public String password;
	public int maxIdleConns;
	public int maxOpenConns;
	public int connMaxLifetime; //秒

	public boolean showSql;
	public String packagesToScan;
	public String configLocations;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
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

	public int getMaxIdleConns() {
		return maxIdleConns;
	}

	public void setMaxIdleConns(int maxIdleConns) {
		this.maxIdleConns = maxIdleConns;
	}

	public int getMaxOpenConns() {
		return maxOpenConns;
	}

	public void setMaxOpenConns(int maxOpenConns) {
		this.maxOpenConns = maxOpenConns;
	}

	public int getConnMaxLifetime() {
		return connMaxLifetime;
	}

	public void setConnMaxLifetime(int connMaxLifetime) {
		this.connMaxLifetime = connMaxLifetime;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public String getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(String packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public String getConfigLocations() {
		return configLocations;
	}

	public void setConfigLocations(String configLocations) {
		this.configLocations = configLocations;
	}

}
