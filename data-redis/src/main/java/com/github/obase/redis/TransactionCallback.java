package com.github.obase.redis;

import redis.clients.jedis.Transaction;

public interface TransactionCallback {

	void doInTransaction(Transaction transaction, Object... args);
}
