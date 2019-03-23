package com.seasun.jx3dc.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.obase.app.App;
import com.github.obase.app.Context;
import com.github.obase.app.Flags;

@Component
public class MyApp extends App {

	static final Logger logger = LogManager.getLogger(MyApp.class);

	@Autowired
	DataSource dataSource;

	@Override
	public void declare(Flags args) {
		args.defArg("test", false, "test mode");
		args.defArg("file", true, "file path here...");
	}

	@Override
	public int execute(Context ctx, Flags args) throws Exception {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("select * from player_match_correlation limit 3");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getString(1));
				logger.info(rs.getString(2));
			}
		} finally {
			conn.close();
		}
		return 0;
	}

}
