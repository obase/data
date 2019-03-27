package com.github.obase.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.obase.redis.JedisCallback;
import com.github.obase.redis.Keyfix;
import com.github.obase.redis.PipelineCallback;
import com.github.obase.redis.RedisClient;
import com.github.obase.redis.TransactionCallback;

import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ListPosition;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

public class RedisServerImpl implements RedisClient, Keyfix {

	final JedisPool jedisPool;
	final String keyfix; // 所有key的统一后缀

	public RedisServerImpl(JedisPool jedisPool, String keyfix) {
		this.jedisPool = jedisPool;
		this.keyfix = keyfix;
	}

	@Override
	public String key(String orgKey) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgKey;
		}
		return new StringBuilder(256).append(orgKey).append('.').append(keyfix).toString();
	}

	@Override
	public String[] keys(String... orgKeys) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgKeys;
		}
		String[] keys = new String[orgKeys.length];
		StringBuilder sb = new StringBuilder(256);
		for (int i = 0; i < keys.length; i++) {
			keys[i] = sb.append(orgKeys[i]).append('.').append(keyfix).toString();
			sb.setLength(0);
		}
		return keys;
	}

	@Override
	public String[] keysvalues(String... orgs) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgs;
		}
		String[] kvs = new String[orgs.length];
		StringBuilder sb = new StringBuilder(256);
		for (int i = 0; i < orgs.length; i++) {
			if (i % 0 == 0) {
				kvs[i] = sb.append(orgs[i]).append('.').append(keyfix).toString();
				sb.setLength(0);
			} else {
				kvs[i] = orgs[i];
			}
		}
		return kvs;
	}

	@Override
	public String[] keys(int n, String... orgs) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgs;
		}
		String[] kvs = new String[orgs.length];
		StringBuilder sb = new StringBuilder(256);
		for (int i = 0; i < n; i++) {
			if (i % 0 == 0) {
				kvs[i] = sb.append(orgs[i]).append('.').append(keyfix).toString();
				sb.setLength(0);
			} else {
				kvs[i] = orgs[i];
			}
		}
		return kvs;
	}

	@Override
	public List<String> keys(List<String> orgs) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgs;
		}
		List<String> keys = new ArrayList<String>(orgs.size());
		StringBuilder sb = new StringBuilder(256);
		for (String org : orgs) {
			keys.add(sb.append(org).append('.').append(keyfix).toString());
			sb.setLength(0);
		}
		return keys;
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key(key), value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String set(String key, String value, SetParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key(key), value, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long persist(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.persist(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String type(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.type(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] dump(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.dump(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String restore(String key, int ttl, byte[] serializedValue) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.restore(key(key), ttl, serializedValue);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String restoreReplace(String key, int ttl, byte[] serializedValue) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.restoreReplace(key(key), ttl, serializedValue);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.expire(key(key), seconds);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pexpire(key(key), milliseconds);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.expireAt(key(key), unixTime);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pexpireAt(key(key), millisecondsTimestamp);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long ttl(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.ttl(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pttl(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pttl(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long touch(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.touch(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setbit(key(key), offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setbit(key(key), offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean getbit(String key, long offset) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getbit(key(key), offset);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setrange(key(key), offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getrange(key(key), startOffset, endOffset);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String getSet(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getSet(key(key), value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long setnx(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setnx(key(key), value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String setex(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setex(key(key), seconds, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String psetex(String key, long milliseconds, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.psetex(key(key), milliseconds, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long decrBy(String key, long decrement) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.decrBy(key(key), decrement);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long decr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.decr(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long incrBy(String key, long increment) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incrBy(key(key), increment);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double incrByFloat(String key, double increment) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incrByFloat(key(key), increment);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long incr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incr(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long append(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.append(key(key), value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String substr(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.substr(key(key), start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hset(key(key), field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hset(String key, Map<String, String> hash) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hset(key(key), hash);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String hget(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hget(key(key), field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hsetnx(key(key), field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmset(key(key), hash);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmget(key(key), fields);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hincrBy(key(key), field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double hincrByFloat(String key, String field, double value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hincrByFloat(key(key), field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean hexists(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hexists(key(key), field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hdel(String key, String... field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hdel(key(key), field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hlen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hlen(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> hkeys(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hkeys(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> hvals(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hvals(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hgetAll(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long rpush(String key, String... string) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpush(key(key), string);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lpush(String key, String... string) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpush(key(key), string);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.llen(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrange(key(key), start, stop);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String ltrim(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.ltrim(key(key), start, stop);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String lindex(String key, long index) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lindex(key(key), index);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String lset(String key, long index, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lset(key(key), index, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lrem(String key, long count, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrem(key(key), count, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String lpop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpop(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String rpop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpop(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sadd(String key, String... member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sadd(key(key), member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> smembers(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smembers(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long srem(String key, String... member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srem(key(key), member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String spop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.spop(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> spop(String key, long count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.spop(key(key), count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scard(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean sismember(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sismember(key(key), member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String srandmember(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srandmember(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> srandmember(String key, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srandmember(key(key), count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long strlen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.strlen(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(String key, double score, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key(key), score, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(String key, double score, String member, ZAddParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key(key), score, member, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key(key), scoreMembers);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key(key), scoreMembers, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrange(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrange(key(key), start, stop);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrem(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrem(key(key), members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zincrby(String key, double increment, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zincrby(key(key), increment, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zincrby(String key, double increment, String member, ZIncrByParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zincrby(key(key), increment, member, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrank(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrank(key(key), member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrevrank(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrank(key(key), member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrange(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key(key), start, stop);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeWithScores(key(key), start, stop);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeWithScores(key(key), start, stop);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcard(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zscore(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscore(key(key), member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> sort(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key(key), sortingParameters);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcount(String key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcount(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcount(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcount(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key(key), min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key(key), min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key(key), min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key(key), min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key(key), min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key(key), min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByRank(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByRank(key(key), start, stop);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByScore(String key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByScore(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByScore(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByScore(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zlexcount(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByLex(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByLex(key(key), min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByLex(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByLex(key(key), min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByLex(key(key), min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long linsert(String key, ListPosition where, String pivot, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.linsert(key(key), where, pivot, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lpushx(String key, String... string) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpushx(key(key), string);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long rpushx(String key, String... string) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpushx(key(key), string);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> blpop(int timeout, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(timeout, key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> brpop(int timeout, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(timeout, key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long del(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.del(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long unlink(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.unlink(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String echo(String string) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.echo(string);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long move(String key, int dbIndex) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.move(key(key), dbIndex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitcount(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitcount(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitcount(key(key), start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitpos(String key, boolean value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitpos(key(key), value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitpos(String key, boolean value, BitPosParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitpos(key(key), value, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hscan(key(key), cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hscan(key(key), cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sscan(key(key), cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscan(key(key), cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscan(key(key), cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sscan(key(key), cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pfadd(String key, String... elements) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfadd(key(key), elements);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public long pfcount(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfcount(key(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long geoadd(String key, double longitude, double latitude, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geoadd(key(key), longitude, latitude, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geoadd(key(key), memberCoordinateMap);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double geodist(String key, String member1, String member2) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geodist(key(key), member1, member2);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double geodist(String key, String member1, String member2, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geodist(key(key), member1, member2, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> geohash(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geohash(key(key), members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoCoordinate> geopos(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geopos(key(key), members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadius(key(key), longitude, latitude, radius, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusReadonly(key(key), longitude, latitude, radius, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadius(key(key), longitude, latitude, radius, unit, param);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusReadonly(key(key), longitude, latitude, radius, unit, param);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusByMember(key(key), member, radius, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusByMemberReadonly(key(key), member, radius, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusByMember(key(key), member, radius, unit, param);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusByMemberReadonly(key(key), member, radius, unit, param);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<Long> bitfield(String key, String... arguments) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitfield(key(key), arguments);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hstrlen(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hstrlen(key(key), field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long del(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.del(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long unlink(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.unlink(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long exists(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> blpop(int timeout, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(timeout, keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> brpop(int timeout, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(timeout, keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> mget(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.mget(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String mset(String... keysvalues) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.mset(keysvalues(keysvalues));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long msetnx(String... keysvalues) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.msetnx(keysvalues(keysvalues));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String rename(String oldkey, String newkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rename(key(oldkey), key(newkey));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long renamenx(String oldkey, String newkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.renamenx(key(oldkey), key(newkey));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String rpoplpush(String srckey, String dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpoplpush(key(srckey), key(dstkey));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> sdiff(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sdiff(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sdiffstore(String dstkey, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sdiffstore(key(dstkey), keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> sinter(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sinter(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sinterstore(String dstkey, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sinterstore(key(dstkey), keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smove(key(srckey), key(dstkey), member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key(key), sortingParameters, key(dstkey));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sort(String key, String dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key(key), key(dstkey));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> sunion(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sunion(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sunionstore(String dstkey, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sunionstore(key(dstkey), keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zinterstore(String dstkey, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zinterstore(key(dstkey), keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zinterstore(String dstkey, ZParams params, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zinterstore(key(dstkey), params, keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zunionstore(String dstkey, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zunionstore(key(dstkey), keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zunionstore(String dstkey, ZParams params, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zunionstore(key(dstkey), params, keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpoplpush(key(source), key(destination), timeout);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long publish(String channel, String message) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.publish(key(channel), message);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.subscribe(jedisPubSub, keys(channels));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.subscribe(jedisPubSub, patterns);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitop(op, key(destKey), keys(srcKeys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String pfmerge(String destkey, String... sourcekeys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfmerge(key(destkey), keys(sourcekeys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public long pfcount(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfcount(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long touch(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.touch(keys(keys));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<String> scan(String cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scan(cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> keys(String pattern) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.keys(pattern);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object eval(String script, int keyCount, String... params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.eval(script, keyCount, keys(keyCount, params));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.eval(script, keys(keys), args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object eval(String script) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.eval(script);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object evalsha(String sha1) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.evalsha(sha1);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.evalsha(sha1, keys(keys), args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object evalsha(String sha1, int keyCount, String... params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.evalsha(sha1, keyCount, keys(keyCount, params));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object provider() {
		return jedisPool;
	}

	@Override
	public List<Object> pipeline(PipelineCallback action) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Pipeline pl = jedis.pipelined();
			action.pipeline(pl);
			return pl.exec().get();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<Object> transaction(TransactionCallback action) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Transaction tx = jedis.multi();
			action.transaction(tx);
			tx.dis
			return tx.exec();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object jedis(JedisCallback action) {
		// TODO Auto-generated method stub
		return null;
	}

}
