package com.github.obase.redis;

import java.util.List;

import redis.clients.jedis.Transaction;

@FunctionalInterface
public interface TransactionCallback {

	List<Object> transaction(Transaction transaction);
}
