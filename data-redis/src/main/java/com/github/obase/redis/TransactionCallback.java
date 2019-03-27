package com.github.obase.redis;

import redis.clients.jedis.Transaction;

@FunctionalInterface
public interface TransactionCallback {
	
	// 如果返回false或抛出异步,则事务回滚
	boolean transaction(Transaction transaction);
}
