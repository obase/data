package com.github.obase.beans;

import java.beans.PropertyVetoException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

import com.github.obase.base.ObjectBase;
import com.github.obase.base.StringBase;
import com.github.obase.mysql.MysqlClient;
import com.github.obase.mysql.impl.MysqlClientImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MysqlClientFactoryBean implements FactoryBean<MysqlClient> {

	static final Logger logger = LogManager.getLogger(MysqlClientFactoryBean.class);

	final MysqlConfig config;

	public MysqlClientFactoryBean(MysqlConfig config) {
		this.config = config;
	}

	@Override
	public MysqlClient getObject() throws Exception {
		if (config.maxIdleConns <= 0) {
			config.maxIdleConns = 8;
		}

		if (config.maxOpenConns <= 0) {
			config.maxOpenConns = 256;
		}

		if (config.connMaxLifetime <= 0) {
			config.connMaxLifetime = 500; // 5分钟无用则关闭,避免重试
		}

		ComboPooledDataSource ds = new ComboPooledDataSource();

		ds.setJdbcUrl("jdbc:mysql://" + config.address + "/" + config.database + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull");
		try {
			ds.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e) {
			logger.error(e);
		}
		ds.setUser(config.username);
		ds.setPassword(config.password);

		ds.setInitialPoolSize(config.maxIdleConns);
		ds.setMinPoolSize(config.maxIdleConns);
		ds.setMaxPoolSize(config.maxOpenConns);
		ds.setIdleConnectionTestPeriod(config.connMaxLifetime);

		// 确保数据源最后自动关闭
		ObjectBase.closeWhenShutdown(new AutoCloseable() {
			public void close() throws Exception {
				ds.close();
			}
		});

		MysqlClientImpl impl = new MysqlClientImpl();
		impl.setDataSource(ds);
		impl.setShowSql(config.showSql);
		if (StringBase.isNotEmpty(config.configLocations)) {
			impl.setConfigLocations(config.configLocations);
		} else {
			impl.setConfigLocations("classpath*:/query/**/*.xml");
		}
		if (StringBase.isNotEmpty(config.packagesToScan)) {
			impl.setPackagesToScan(config.packagesToScan);
		}
		impl.init();
		return impl;
	}

	@Override
	public Class<?> getObjectType() {
		return MysqlClient.class;
	}

}
