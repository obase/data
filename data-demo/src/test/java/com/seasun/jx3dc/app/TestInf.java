package com.seasun.jx3dc.app;

import java.util.Map;

import com.github.obase.base.ConfBase;
import com.github.obase.beans.RedisConfig;

public class TestInf {

	public static void main(String[] args) {
		ConfBase.reset();
		Map<String, Object> conf = (Map<String, Object>) ConfBase.get("redis.0");
		System.out.println(conf);
		RedisConfig config = new RedisConfig();
		config = ConfBase.bind("redis.0", config);
		System.out.println(config);
	}
}
