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

@Component
public class MyApp extends App {

	static final Logger logger = LogManager.getLogger(MyApp.class);

	@Autowired
	MysqlClient mysql;

	@Override
	public void declare(Flags args) {
		args.defArg("test", false, "test mode");
		args.defArg("file", true, "file path here...");
	}

	@Override
	public int execute(Context ctx, Flags args) throws Exception {
		List<Map> list = mysql.query("test.selectAccount", null, null);
		System.out.println(list);
		logger.info(list);
		return 0;
	}

}
