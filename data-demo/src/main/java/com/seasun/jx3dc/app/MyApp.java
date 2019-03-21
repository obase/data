package com.seasun.jx3dc.app;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.obase.app.App;
import com.github.obase.app.Args;
import com.github.obase.app.Context;

public class MyApp extends App {

	static final Logger logger = LogManager.getLogger(MyApp.class);

	@Override
	public int execute(Context ctx, Args args) throws Exception {
		for (int i = 0; i < 100000; i++) {
			logger.info("这是来自log的日志....");
			logger.info("this is a message form logger");
			System.out.println("this is in the TestMain class");
			TimeUnit.SECONDS.sleep(1);
		}
		return 0;
	}

}