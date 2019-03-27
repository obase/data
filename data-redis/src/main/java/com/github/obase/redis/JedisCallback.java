package com.github.obase.redis;

import redis.clients.jedis.Jedis;

@FunctionalInterface
public interface JedisCallback {
	Object jedis(Jedis jedis);
}
