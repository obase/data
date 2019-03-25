package com.github.obase.beans;

import java.util.Map;

public class MongoConfig {
	String key;
	String address;
	String username;
	String password;
	String database;
	@Deprecated
	String source; // 改用authSource
	String authDatabase;
	String authSource;
	String mode;
	Map<String, Object> safe;
	int connectTimeout; // 秒
	int keepalive; //秒
	int readTimeout;
	int writeTimeout;
	int minPoolSize;
	int maxPoolSize;
	int maxPoolWaitTimeMS;
	int maxPoolIdleTimeMS;

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

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAuthDatabase() {
		return authDatabase;
	}

	public void setAuthDatabase(String authDatabase) {
		this.authDatabase = authDatabase;
	}

	public String getAuthSource() {
		return authSource;
	}

	public void setAuthSource(String authSource) {
		this.authSource = authSource;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Map<String, Object> getSafe() {
		return safe;
	}

	public void setSafe(Map<String, Object> safe) {
		this.safe = safe;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getKeepalive() {
		return keepalive;
	}

	public void setKeepalive(int keepalive) {
		this.keepalive = keepalive;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getWriteTimeout() {
		return writeTimeout;
	}

	public void setWriteTimeout(int writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMaxPoolWaitTimeMS() {
		return maxPoolWaitTimeMS;
	}

	public void setMaxPoolWaitTimeMS(int maxPoolWaitTimeMS) {
		this.maxPoolWaitTimeMS = maxPoolWaitTimeMS;
	}

	public int getMaxPoolIdleTimeMS() {
		return maxPoolIdleTimeMS;
	}

	public void setMaxPoolIdleTimeMS(int maxPoolIdleTimeMS) {
		this.maxPoolIdleTimeMS = maxPoolIdleTimeMS;
	}

}
