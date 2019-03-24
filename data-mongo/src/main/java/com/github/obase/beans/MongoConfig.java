package com.github.obase.beans;

import java.util.Map;

public class MongoConfig {
	String key;
	String address;
	String database;
	String username;
	String password;
	String source;
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
}
