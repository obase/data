package com.seasun.jx3dc.app;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.obase.app.App;
import com.github.obase.app.Context;
import com.github.obase.app.Flags;
import com.github.obase.mysql.MysqlClient;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

@Component
public class MyApp extends App {

	static final Logger logger = LogManager.getLogger(MyApp.class);

	@Autowired
	MysqlClient mysql;

	@Autowired
	MongoClient mongo;

	@Override
	public void declare(Flags args) {
		args.defArg("test", false, "test mode");
		args.defArg("file", true, "file path here...");
	}

	@Override
	public int execute(Context ctx, Flags args) throws Exception {
		@SuppressWarnings("rawtypes")
		List<Map> list = mysql.query("test.selectAccount", null, null);
		System.out.println(list);
		logger.info(list);

		MongoDatabase db = mongo.getDatabase("jx3activity");
		MongoIterable<String> names = db.listCollectionNames();
		for (String name : names) {
			System.out.println(name);
		}

		return 0;
	}

}
