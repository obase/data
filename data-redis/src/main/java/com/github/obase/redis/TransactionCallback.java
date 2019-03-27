package com.github.obase.redis;

import redis.clients.jedis.Transaction;

@FunctionalInterface
public interface TransactionCallback {

	void transaction(Transaction transaction);
}
