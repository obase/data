package com.github.obase.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

public class TestMain {

	public static void main(String[] args) {
//		HostAndPort hp = new HostAndPort("120.92.169.81", 7000);
//		JedisCluster jc = new JedisCluster(hp, 20000, 20000, 3, "KingSoft1239002nx624@a123", new GenericObjectPoolConfig());
//
//		System.out.println(jc.set("abc", "123456789"));
//
//		jc.close();
//
//		
		
		JedisPool jp = new JedisPool("localhost");
		Jedis j = jp.getResource();
		
		Pipeline p = j.pipelined();
		p.exec();
		
		j.close();
		jp.close();
		
		Transaction tx = j.multi();
		tx.exec();
		
		System.out.println("done");
		
	}

}
