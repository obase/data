package com.github.obase.redis.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.obase.redis.JedisCallback;
import com.github.obase.redis.PipelineCallback;
import com.github.obase.redis.RedisClient;
import com.github.obase.redis.TransactionCallback;

import redis.clients.jedis.BitOP;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ListPosition;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

public class RedisClusterImpl extends KeyfixImpl implements RedisClient {

	final JedisCluster jedisCluster;

	public RedisClusterImpl(JedisCluster jedisCluster, String keyfix) {
		super(keyfix);
		this.jedisCluster = jedisCluster;
	}

	@Override
	public String set(String key, String value) {
		return jedisCluster.set(key(key), value);
	}

	@Override
	public String set(String key, String value, SetParams params) {
		return jedisCluster.set(key(key), value, params);
	}

	@Override
	public String get(String key) {
		return jedisCluster.get(key(key));
	}

	@Override
	public Boolean exists(String key) {
		return jedisCluster.exists(key(key));
	}

	@Override
	public Long persist(String key) {
		return jedisCluster.persist(key(key));
	}

	@Override
	public String type(String key) {
		return jedisCluster.type(key(key));
	}

	@Override
	public byte[] dump(String key) {
		return jedisCluster.dump(key(key));
	}

	@Override
	public String restore(String key, int ttl, byte[] serializedValue) {
		return jedisCluster.restore(key(key), ttl, serializedValue);
	}

	@Override
	public Long expire(String key, int seconds) {
		return jedisCluster.expire(key(key), seconds);
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		return jedisCluster.pexpire(key(key), milliseconds);
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		return jedisCluster.expireAt(key(key), unixTime);
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {
		return jedisCluster.pexpireAt(key(key), millisecondsTimestamp);
	}

	@Override
	public Long ttl(String key) {
		return jedisCluster.ttl(key(key));
	}

	@Override
	public Long pttl(String key) {
		return jedisCluster.pttl(key(key));
	}

	@Override
	public Long touch(String key) {
		return jedisCluster.touch(key(key));
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		return jedisCluster.setbit(key(key), offset, value);
	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		return jedisCluster.setbit(key(key), offset, value);
	}

	@Override
	public Boolean getbit(String key, long offset) {
		return jedisCluster.getbit(key(key), offset);
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		return jedisCluster.setrange(key(key), offset, value);
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		return jedisCluster.getrange(key(key), startOffset, endOffset);
	}

	@Override
	public String getSet(String key, String value) {
		return jedisCluster.getSet(key(key), value);
	}

	@Override
	public Long setnx(String key, String value) {
		return jedisCluster.setnx(key(key), value);
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return jedisCluster.setex(key(key), seconds, value);
	}

	@Override
	public String psetex(String key, long milliseconds, String value) {
		return jedisCluster.psetex(key(key), milliseconds, value);
	}

	@Override
	public Long decrBy(String key, long decrement) {
		return jedisCluster.decrBy(key(key), decrement);
	}

	@Override
	public Long decr(String key) {
		return jedisCluster.decr(key(key));
	}

	@Override
	public Long incrBy(String key, long increment) {
		return jedisCluster.incrBy(key(key), increment);
	}

	@Override
	public Double incrByFloat(String key, double increment) {
		return jedisCluster.incrByFloat(key(key), increment);
	}

	@Override
	public Long incr(String key) {
		return jedisCluster.incr(key(key));
	}

	@Override
	public Long append(String key, String value) {
		return jedisCluster.append(key(key), value);
	}

	@Override
	public String substr(String key, int start, int end) {
		return jedisCluster.substr(key(key), start, end);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return jedisCluster.hset(key(key), field, value);
	}

	@Override
	public Long hset(String key, Map<String, String> hash) {
		return jedisCluster.hset(key(key), hash);
	}

	@Override
	public String hget(String key, String field) {
		return jedisCluster.hget(key(key), field);
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		return jedisCluster.hsetnx(key(key), field, value);
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		return jedisCluster.hmset(key(key), hash);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return jedisCluster.hmget(key(key), fields);
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		return jedisCluster.hincrBy(key(key), field, value);
	}

	@Override
	public Boolean hexists(String key, String field) {
		return jedisCluster.hexists(key(key), field);
	}

	@Override
	public Long hdel(String key, String... field) {
		return jedisCluster.hdel(key(key), field);
	}

	@Override
	public Long hlen(String key) {
		return jedisCluster.hlen(key(key));
	}

	@Override
	public Set<String> hkeys(String key) {
		return jedisCluster.hkeys(key(key));
	}

	@Override
	public List<String> hvals(String key) {
		return jedisCluster.hvals(key(key));
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		return jedisCluster.hgetAll(key(key));
	}

	@Override
	public Long rpush(String key, String... string) {
		return jedisCluster.rpush(key(key), string);
	}

	@Override
	public Long lpush(String key, String... string) {
		return jedisCluster.lpush(key(key), string);
	}

	@Override
	public Long llen(String key) {
		return jedisCluster.llen(key(key));
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {
		return jedisCluster.lrange(key(key), start, stop);
	}

	@Override
	public String ltrim(String key, long start, long stop) {
		return jedisCluster.ltrim(key(key), start, stop);
	}

	@Override
	public String lindex(String key, long index) {
		return jedisCluster.lindex(key(key), index);
	}

	@Override
	public String lset(String key, long index, String value) {
		return jedisCluster.lset(key(key), index, value);
	}

	@Override
	public Long lrem(String key, long count, String value) {
		return jedisCluster.lrem(key(key), count, value);
	}

	@Override
	public String lpop(String key) {
		return jedisCluster.lpop(key(key));
	}

	@Override
	public String rpop(String key) {
		return jedisCluster.rpop(key(key));
	}

	@Override
	public Long sadd(String key, String... member) {
		return jedisCluster.sadd(key(key), member);
	}

	@Override
	public Set<String> smembers(String key) {
		return jedisCluster.smembers(key(key));
	}

	@Override
	public Long srem(String key, String... member) {
		return jedisCluster.srem(key(key), member);
	}

	@Override
	public String spop(String key) {
		return jedisCluster.spop(key(key));
	}

	@Override
	public Set<String> spop(String key, long count) {
		return jedisCluster.spop(key(key), count);
	}

	@Override
	public Long scard(String key) {
		return jedisCluster.scard(key(key));
	}

	@Override
	public Boolean sismember(String key, String member) {
		return jedisCluster.sismember(key(key), member);
	}

	@Override
	public String srandmember(String key) {
		return jedisCluster.srandmember(key(key));
	}

	@Override
	public List<String> srandmember(String key, int count) {
		return jedisCluster.srandmember(key(key), count);
	}

	@Override
	public Long strlen(String key) {
		return jedisCluster.strlen(key(key));
	}

	@Override
	public Long zadd(String key, double score, String member) {
		return jedisCluster.zadd(key(key), score, member);
	}

	@Override
	public Long zadd(String key, double score, String member, ZAddParams params) {
		return jedisCluster.zadd(key(key), score, member, params);
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		return jedisCluster.zadd(key(key), scoreMembers);
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
		return jedisCluster.zadd(key(key), scoreMembers, params);
	}

	@Override
	public Set<String> zrange(String key, long start, long stop) {
		return jedisCluster.zrange(key(key), start, stop);
	}

	@Override
	public Long zrem(String key, String... members) {
		return jedisCluster.zrem(key(key), members);
	}

	@Override
	public Double zincrby(String key, double increment, String member) {
		return jedisCluster.zincrby(key(key), increment, member);
	}

	@Override
	public Double zincrby(String key, double increment, String member, ZIncrByParams params) {
		return jedisCluster.zincrby(key(key), increment, member, params);
	}

	@Override
	public Long zrank(String key, String member) {
		return jedisCluster.zrank(key(key), member);
	}

	@Override
	public Long zrevrank(String key, String member) {
		return jedisCluster.zrevrank(key(key), member);
	}

	@Override
	public Set<String> zrevrange(String key, long start, long stop) {
		return jedisCluster.zrevrange(key(key), start, stop);
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
		return jedisCluster.zrangeWithScores(key(key), start, stop);
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
		return jedisCluster.zrevrangeWithScores(key(key), start, stop);
	}

	@Override
	public Long zcard(String key) {
		return jedisCluster.zcard(key(key));
	}

	@Override
	public Double zscore(String key, String member) {
		return jedisCluster.zscore(key(key), member);
	}

	@Override
	public List<String> sort(String key) {
		return jedisCluster.sort(key(key));
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		return jedisCluster.sort(key(key), sortingParameters);
	}

	@Override
	public Long zcount(String key, double min, double max) {
		return jedisCluster.zcount(key(key), min, max);
	}

	@Override
	public Long zcount(String key, String min, String max) {
		return jedisCluster.zcount(key(key), min, max);
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		return jedisCluster.zrangeByScore(key(key), min, max);
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		return jedisCluster.zrangeByScore(key(key), min, max);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		return jedisCluster.zrevrangeByScore(key(key), max, min);
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		return jedisCluster.zrangeByScore(key(key), min, max);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		return jedisCluster.zrevrangeByScore(key(key), max, min);
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		return jedisCluster.zrangeByScore(key(key), min, max, offset, count);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		return jedisCluster.zrevrangeByScore(key(key), max, min, offset, count);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		return jedisCluster.zrangeByScoreWithScores(key(key), min, max);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		return jedisCluster.zrangeByScoreWithScores(key(key), max, min);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		return jedisCluster.zrangeByScoreWithScores(key(key), min, max, offset, count);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		return jedisCluster.zrevrangeByScore(key(key), max, min, offset, count);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		return jedisCluster.zrangeByScoreWithScores(key(key), min, max);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		return jedisCluster.zrevrangeByScoreWithScores(key(key), max, min);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		return jedisCluster.zrangeByScoreWithScores(key(key), min, max, offset, count);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		return jedisCluster.zrevrangeByScoreWithScores(key(key), max, min, offset, count);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		return jedisCluster.zrevrangeByScoreWithScores(key(key), max, min, offset, count);
	}

	@Override
	public Long zremrangeByRank(String key, long start, long stop) {
		return jedisCluster.zremrangeByRank(key(key), start, stop);
	}

	@Override
	public Long zremrangeByScore(String key, double min, double max) {
		return jedisCluster.zremrangeByScore(key(key), min, max);
	}

	@Override
	public Long zremrangeByScore(String key, String min, String max) {
		return jedisCluster.zremrangeByScore(key(key), min, max);
	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		return jedisCluster.zlexcount(key(key), min, max);
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		return jedisCluster.zrangeByLex(key(key), min, max);
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		return jedisCluster.zrangeByLex(key(key), min, max, offset, count);
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min) {
		return jedisCluster.zrevrangeByLex(key(key), max, min);
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
		return jedisCluster.zrevrangeByLex(key(key), max, min, offset, count);
	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		return jedisCluster.zremrangeByLex(key(key), min, max);
	}

	@Override
	public Long linsert(String key, ListPosition where, String pivot, String value) {
		return jedisCluster.linsert(key(key), where, pivot, value);
	}

	@Override
	public Long lpushx(String key, String... string) {
		return jedisCluster.lpushx(key(key), string);
	}

	@Override
	public Long rpushx(String key, String... string) {
		return jedisCluster.rpushx(key(key), string);
	}

	@Override
	public List<String> blpop(int timeout, String key) {
		return jedisCluster.blpop(timeout, key(key));
	}

	@Override
	public List<String> brpop(int timeout, String key) {
		return jedisCluster.brpop(timeout, key(key));
	}

	@Override
	public Long del(String key) {
		return jedisCluster.del(key(key));
	}

	@Override
	public Long unlink(String key) {
		return jedisCluster.unlink(key(key));
	}

	@Override
	public String echo(String string) {
		return jedisCluster.echo(string);
	}

	@Override
	public Long bitcount(String key) {
		return jedisCluster.bitcount(key(key));
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		return jedisCluster.bitcount(key(key), start, end);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		return jedisCluster.hscan(key(key), cursor);
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		return jedisCluster.sscan(key(key), cursor);
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		return jedisCluster.zscan(key(key), cursor);
	}

	@Override
	public Long pfadd(String key, String... elements) {
		return jedisCluster.pfadd(key(key), elements);
	}

	@Override
	public long pfcount(String key) {
		return jedisCluster.pfcount(key(key));
	}

	@Override
	public Long geoadd(String key, double longitude, double latitude, String member) {
		return jedisCluster.geoadd(key(key), longitude, latitude, member);
	}

	@Override
	public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
		return jedisCluster.geoadd(key(key), memberCoordinateMap);
	}

	@Override
	public Double geodist(String key, String member1, String member2) {
		return jedisCluster.geodist(key(key), member1, member2);
	}

	@Override
	public Double geodist(String key, String member1, String member2, GeoUnit unit) {
		return jedisCluster.geodist(key(key), member1, member2, unit);
	}

	@Override
	public List<String> geohash(String key, String... members) {
		return jedisCluster.geohash(key(key), members);
	}

	@Override
	public List<GeoCoordinate> geopos(String key, String... members) {
		return jedisCluster.geopos(key(key), members);
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
		return jedisCluster.georadius(key(key), longitude, latitude, radius, unit);
	}

	@Override
	public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit) {
		return jedisCluster.georadiusReadonly(key(key), longitude, latitude, radius, unit);
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
		return jedisCluster.georadius(key(key), longitude, latitude, radius, unit, param);
	}

	@Override
	public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
		return jedisCluster.georadiusReadonly(key(key), longitude, latitude, radius, unit, param);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
		return jedisCluster.georadiusByMember(key(key), member, radius, unit);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit) {
		return jedisCluster.georadiusByMemberReadonly(key(key), member, radius, unit);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
		return jedisCluster.georadiusByMember(key(key), member, radius, unit, param);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
		return jedisCluster.georadiusByMemberReadonly(key(key), member, radius, unit, param);
	}

	@Override
	public List<Long> bitfield(String key, String... arguments) {
		return jedisCluster.bitfield(key(key), arguments);
	}

	@Override
	public Long hstrlen(String key, String field) {
		return jedisCluster.hstrlen(key(key), field);
	}

	@Override
	public Long del(String... keys) {
		return jedisCluster.del(keys(keys));
	}

	@Override
	public Long unlink(String... keys) {
		return jedisCluster.unlink(keys(keys));
	}

	@Override
	public Long exists(String... keys) {
		return jedisCluster.exists(keys(keys));
	}

	@Override
	public List<String> blpop(int timeout, String... keys) {
		return jedisCluster.blpop(timeout, keys(keys));
	}

	@Override
	public List<String> brpop(int timeout, String... keys) {
		return jedisCluster.brpop(timeout, keys(keys));
	}

	@Override
	public List<String> mget(String... keys) {
		return jedisCluster.mget(keys(keys));
	}

	@Override
	public String mset(String... keysvalues) {
		return jedisCluster.mset(keysvalues(keysvalues));
	}

	@Override
	public Long msetnx(String... keysvalues) {
		return jedisCluster.msetnx(keysvalues(keysvalues));
	}

	@Override
	public String rename(String oldkey, String newkey) {
		return jedisCluster.rename(key(oldkey), key(newkey));
	}

	@Override
	public Long renamenx(String oldkey, String newkey) {
		return jedisCluster.renamenx(key(oldkey), key(newkey));
	}

	@Override
	public String rpoplpush(String srckey, String dstkey) {
		return jedisCluster.rpoplpush(key(srckey), key(dstkey));
	}

	@Override
	public Set<String> sdiff(String... keys) {
		return jedisCluster.sdiff(keys(keys));
	}

	@Override
	public Long sdiffstore(String dstkey, String... keys) {
		return jedisCluster.sdiffstore(key(dstkey), keys(keys));
	}

	@Override
	public Set<String> sinter(String... keys) {
		return jedisCluster.sinter(keys(keys));
	}

	@Override
	public Long sinterstore(String dstkey, String... keys) {
		return jedisCluster.sinterstore(key(dstkey), keys(keys));
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {
		return jedisCluster.smove(key(srckey), key(dstkey), member);
	}

	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {
		return jedisCluster.sort(key(key), sortingParameters, key(dstkey));
	}

	@Override
	public Long sort(String key, String dstkey) {
		return jedisCluster.sort(key(key), key(dstkey));
	}

	@Override
	public Set<String> sunion(String... keys) {
		return jedisCluster.sunion(keys(keys));
	}

	@Override
	public Long sunionstore(String dstkey, String... keys) {
		return jedisCluster.sunionstore(key(dstkey), keys(keys));
	}

	@Override
	public Long zinterstore(String dstkey, String... keys) {
		return jedisCluster.zinterstore(key(dstkey), keys(keys));
	}

	@Override
	public Long zinterstore(String dstkey, ZParams params, String... keys) {
		return jedisCluster.zinterstore(key(dstkey), params, keys(keys));
	}

	@Override
	public Long zunionstore(String dstkey, String... keys) {
		return jedisCluster.zunionstore(key(dstkey), keys(keys));
	}

	@Override
	public Long zunionstore(String dstkey, ZParams params, String... keys) {
		return jedisCluster.zunionstore(key(dstkey), params, keys(keys));
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		return jedisCluster.brpoplpush(key(source), key(destination), timeout);
	}

	@Override
	public Long publish(String channel, String message) {
		return jedisCluster.publish(key(channel), message);
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		jedisCluster.subscribe(jedisPubSub, keys(channels));
	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		jedisCluster.psubscribe(jedisPubSub, patterns);
	}

	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {
		return jedisCluster.bitop(op, key(destKey), keys(srcKeys));
	}

	@Override
	public String pfmerge(String destkey, String... sourcekeys) {
		return jedisCluster.pfmerge(key(destkey), keys(sourcekeys));
	}

	@Override
	public long pfcount(String... keys) {
		return jedisCluster.pfcount(keys(keys));
	}

	@Override
	public Long touch(String... keys) {
		return jedisCluster.touch(keys(keys));
	}

	@Override
	public ScanResult<String> scan(String cursor, ScanParams params) {
		return jedisCluster.scan(cursor, params);
	}

	@Override
	public Set<String> keys(String pattern) {
		return jedisCluster.keys(pattern);
	}

	@Override
	public Object eval(String script, int keyCount, String... params) {
		return jedisCluster.eval(script, keyCount, keys(keyCount, params));
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		return jedisCluster.eval(script, keys(keys), args);
	}

	@Override
	public Object eval(String script, String sampleKey) {
		return jedisCluster.eval(script, key(sampleKey));
	}

	@Override
	public Object evalsha(String sha1, String sampleKey) {
		return jedisCluster.evalsha(sha1, key(sampleKey));
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		return jedisCluster.evalsha(sha1, keys(keys), args);
	}

	@Override
	public Object evalsha(String sha1, int keyCount, String... params) {
		return jedisCluster.evalsha(sha1, keyCount, keys(keyCount, params));
	}

	@Override
	public List<Boolean> scriptExists(String sampleKey, String... sha1) {
		return jedisCluster.scriptExists(sampleKey, sha1);
	}

	@Override
	public String scriptLoad(String script, String sampleKey) {
		return jedisCluster.scriptLoad(script, sampleKey);
	}

	@Override
	public Object provider() {
		return jedisCluster;
	}

	@Override
	public List<Object> pipeline(PipelineCallback action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Object> transaction(TransactionCallback action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object jedis(JedisCallback action) {
		throw new UnsupportedOperationException();
	}

}
