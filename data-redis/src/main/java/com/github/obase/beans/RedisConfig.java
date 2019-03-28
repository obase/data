package com.github.obase.beans;

import java.util.Map;

public class RedisConfig {

	String key;
	String address;
	boolean cluster;
	int database; // 向前兼容
	String password;

	int keepalive; //秒
	int connectTimeout; //秒
	int readTimeout; //秒
	int writeTimeout; //秒
	int initConns;
	int maxConns;

	int maxIdles;
	int testIdleTimeout; //秒
	boolean errExceMaxConns;
	String keyfix;
	Map<String, String> proxyips;

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

	public boolean isCluster() {
		return cluster;
	}

	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getKeepalive() {
		return keepalive;
	}

	public void setKeepalive(int keepalive) {
		this.keepalive = keepalive;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
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

	public int getInitConns() {
		return initConns;
	}

	public void setInitConns(int initConns) {
		this.initConns = initConns;
	}

	public int getMaxConns() {
		return maxConns;
	}

	public void setMaxConns(int maxConns) {
		this.maxConns = maxConns;
	}

	public int getMaxIdles() {
		return maxIdles;
	}

	public void setMaxIdles(int maxIdles) {
		this.maxIdles = maxIdles;
	}

	public int getTestIdleTimeout() {
		return testIdleTimeout;
	}

	public void setTestIdleTimeout(int testIdleTimeout) {
		this.testIdleTimeout = testIdleTimeout;
	}

	public boolean isErrExceMaxConns() {
		return errExceMaxConns;
	}

	public void setErrExceMaxConns(boolean errExceMaxConns) {
		this.errExceMaxConns = errExceMaxConns;
	}

	public String getKeyfix() {
		return keyfix;
	}

	public void setKeyfix(String keyfix) {
		this.keyfix = keyfix;
	}

	public Map<String, String> getProxyips() {
		return proxyips;
	}

	public void setProxyips(Map<String, String> proxyips) {
		this.proxyips = proxyips;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

}
