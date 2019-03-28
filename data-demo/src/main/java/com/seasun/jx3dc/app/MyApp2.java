package com.seasun.jx3dc.app;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.github.obase.app.App;
import com.github.obase.app.Flags;
import com.github.obase.redis.RedisClient;
import com.github.obase.app.Context;

@Component
public class MyApp2 extends App {

	static final Logger logger = LogManager.getLogger(MyApp2.class);

	@Autowired
	@Qualifier("localredis")
	RedisClient redisClient;

	@Override
	public void declare(Flags args) {
		args.defArg("test", false, "test mode");
		args.defArg("file", true, "file path here...");
	}

	@Override
	public int execute(Context ctx, Flags args) throws Exception {
		for (int i = 0; i < 100; i++) {
			redisClient.set("key" + i, "val" + i);
			logger.info("这是来自log的日志....");
			logger.info("this is a message form logger");
			System.out.println("this is in the TestMain class");
			TimeUnit.SECONDS.sleep(1);
		}
		return 0;
	}

}
