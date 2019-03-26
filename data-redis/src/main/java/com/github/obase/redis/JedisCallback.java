package com.github.obase.redis;

import redis.clients.jedis.Jedis;

public interface JedisCallback<T> {

	T doInJedis(Jedis jedis, Object... args);

}
