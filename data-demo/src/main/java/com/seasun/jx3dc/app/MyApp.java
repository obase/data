package com.seasun.jx3dc.app;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.github.obase.app.App;
import com.github.obase.app.Context;
import com.github.obase.app.Flags;
import com.github.obase.mysql.MysqlClient;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.seasun.jx3dc.app.comp.Bean1;

@Component
public class MyApp extends App {

	static final Logger logger = LogManager.getLogger(MyApp.class);

	@Autowired
	@Qualifier("account")
	MysqlClient mysql;

	@Autowired
	@Qualifier("localmgo")
	MongoClient mongo;

	@Override
	public void declare(Flags args) {
		args.defArg("test", false, "test mode");
		args.defArg("file", true, "file path here...");
	}

	@Override
	public int execute(Context ctx, Flags args) throws Exception {
		List<Bean1> list = mysql.query("test.selectAccount", Bean1.class, null);
		System.out.println(list);
		logger.info(list);
		
		mysql.selectByKey(Bean1.class, "a");

		_OUTER_: for (int i = 0; i < 5; i++) {
			try {
				mysql.transaction(t -> {
					return null;
				});
				break _OUTER_;
			} catch (Exception e) {
				logger.error(e);
				TimeUnit.SECONDS.sleep(1);
			}
		}

		MongoDatabase db = mongo.getDatabase("jx3activity");
		MongoIterable<String> names = db.listCollectionNames();
		for (String name : names) {
			System.out.println(name);
		}

		return 0;
	}

}
