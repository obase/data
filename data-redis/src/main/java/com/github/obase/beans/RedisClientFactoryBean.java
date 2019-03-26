package com.github.obase.beans;

import org.springframework.beans.factory.FactoryBean;

import com.github.obase.redis.RedisClient;
import com.github.obase.redis.impl.RedisClusterImpl;
import com.github.obase.redis.impl.RedisServerImpl;

import redis.clients.jedis.JedisPool;

public class RedisClientFactoryBean implements FactoryBean<RedisClient> {

	final RedisConfig config;

	public RedisClientFactoryBean(RedisConfig config) {
		this.config = config;
	}

	@Override
	public RedisClient getObject() throws Exception {
		if (config.cluster) {
			return newRedisClusterImpl(config);
		} else {
			return newRedisServer(config);
		}
	}

	@Override
	public Class<?> getObjectType() {
		return RedisClient.class;
	}

	private RedisServerImpl newRedisServer(RedisConfig config) {
		
		
		JedisPool pool = new JedisPool()
		
		return null;
	}

	private RedisClusterImpl newRedisClusterImpl(RedisConfig config) {
		return null;
	}
	
	
}
