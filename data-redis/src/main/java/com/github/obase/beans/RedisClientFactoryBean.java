package com.github.obase.beans;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;

import com.github.obase.base.ConfBase;
import com.github.obase.base.ObjectBase;
import com.github.obase.base.StringBase;
import com.github.obase.redis.RedisClient;
import com.github.obase.redis.impl.RedisClusterImpl;
import com.github.obase.redis.impl.RedisServerImpl;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

public class RedisClientFactoryBean implements FactoryBean<RedisClient> {

	final RedisConfig config;

	public RedisClientFactoryBean(RedisConfig config) {
		this.config = config;
	}

	@Override
	public RedisClient getObject() throws Exception {
		Option opt = parseRedisConfig(config);
		if (config.cluster) {
			JedisCluster jedisCluster = new JedisCluster(opt.nodes, opt.connectionTimeout, opt.soTimeout, Option.DEFAULT_MAX_ATTEMPTS, opt.password, opt.config);
			return new RedisClusterImpl(jedisCluster, config.keyfix);
		} else {
			HostAndPort hp = ObjectBase.first(opt.nodes);
			JedisPool jedisPool = new JedisPool(opt.config, hp.getHost(), hp.getPort(), opt.connectionTimeout, opt.soTimeout, opt.password, opt.database, null);
			return new RedisServerImpl(jedisPool, config.keyfix);
		}
	}

	@Override
	public Class<?> getObjectType() {
		return RedisClient.class;
	}

	private static Set<HostAndPort> parseHostAndPort(String address) {

		if (StringBase.isEmpty(address)) {
			return Collections.emptySet();
		}

		String[] hps = address.split("\\s*,\\s*");
		Set<HostAndPort> sets = new LinkedHashSet<HostAndPort>(hps.length);
		for (String hp : hps) {
			String h = null;
			String p = null;

			int pos = hp.indexOf(':');
			if (pos == -1) {
				h = hp;
			} else {
				h = hp.substring(0, pos);
				p = hp.substring(pos + 1);
			}
			sets.add(new HostAndPort(h, ConfBase.toInteger(p, 6379)));
		}
		return sets;
	}

	static class Option {

		static final int DEFAULT_MAX_ATTEMPTS = 5;

		GenericObjectPoolConfig config;
		Set<HostAndPort> nodes;
		int connectionTimeout;
		int soTimeout;
		String password;
		int database;
	}

	private static Option parseRedisConfig(RedisConfig config) {
		Option opt = new Option();

		opt.config = new GenericObjectPoolConfig();
		opt.nodes = parseHostAndPort(config.address);
		opt.connectionTimeout = config.connectTimeout;
		if (opt.connectionTimeout <= 0) {
			opt.connectionTimeout = Protocol.DEFAULT_TIMEOUT;
		}
		opt.soTimeout = Math.max(config.readTimeout, config.writeTimeout);
		if (opt.soTimeout <= 0) {
			opt.soTimeout = Protocol.DEFAULT_TIMEOUT;
		}
		opt.password = config.password;
		opt.database = config.database;
		if (opt.database < 0) {
			opt.database = Protocol.DEFAULT_DATABASE;
		}

		if (config.maxIdles > 0)
			opt.config.setMaxIdle(config.maxIdles);
		if (config.maxConns > 0)
			opt.config.setMaxTotal(config.maxConns);
		if (config.initConns > 0)
			opt.config.setMinIdle(config.initConns);
		if (config.errExceMaxConns)
			opt.config.setMaxWaitMillis(0);
		opt.config.setTestWhileIdle(true);

		return opt;
	}

}
