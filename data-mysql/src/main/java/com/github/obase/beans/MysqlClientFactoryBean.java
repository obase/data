package com.github.obase.beans;

import java.beans.PropertyVetoException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.obase.base.ObjectBase;
import com.github.obase.base.StringBase;
import com.github.obase.mysql.MysqlClient;
import com.github.obase.mysql.impl.MysqlClientImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MysqlClientFactoryBean implements FactoryBean<MysqlClient>, ApplicationContextAware {

	static final Logger logger = LogManager.getLogger(MysqlClientFactoryBean.class);

	final MysqlConfig config;
	ApplicationContext applicationContext;

	public MysqlClientFactoryBean(MysqlConfig config) {
		this.config = config;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
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

		MysqlConfig builtin;
		Map<String, MysqlConfig> beans = applicationContext.getBeansOfType(MysqlConfig.class);
		if (beans.size() == 1) {
			builtin = beans.values().iterator().next();
		} else {
			builtin = beans.get(MysqlConfig.BUILTIN_MYSQL_CONFIG_NAME);
		}

		if (builtin != null && StringBase.isNotEmpty(builtin.configLocations)) {
			impl.setConfigLocations(builtin.configLocations);
		} else {
			impl.setConfigLocations("classpath*:/query/**/*.xml");
		}
		if (builtin != null && StringBase.isNotEmpty(builtin.packagesToScan)) {
			impl.setPackagesToScan(builtin.packagesToScan);
		}
		if (builtin != null && builtin.updateTable) {
			impl.setUpdateTable(builtin.updateTable);
		}
		impl.init();
		return impl;
	}

	@Override
	public Class<?> getObjectType() {
		return MysqlClient.class;
	}

}
