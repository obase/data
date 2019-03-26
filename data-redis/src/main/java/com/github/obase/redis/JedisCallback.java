package com.github.obase.redis;

import redis.clients.jedis.Jedis;

@FunctionalInterface
public interface JedisCallback<T> {
	T jedis(Jedis jedis);
}
