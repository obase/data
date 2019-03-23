package com.github.obase.beans;

import java.beans.PropertyVetoException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import com.github.obase.base.ConfBase;
import com.github.obase.base.ObjectBase;
import com.github.obase.base.StringBase;
import com.github.obase.mysql.MysqlConfig;
import com.github.obase.mysql.impl.MysqlClientImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MysqlClientDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	static final Logger logger = LogManager.getLogger(MysqlClientDefinitionRegistryPostProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		List<MysqlConfig> configs = ConfBase.bindObjectList("mysql", MysqlConfig.class);
		if (configs.size() > 0) {
			for (MysqlConfig config : configs) {

				logger.info("init mysql bean: key={}, address={}, database={}, username={}, maxIdleConns={}, maxOpenConns={}, connMaxLifetime={}", config.key, config.address, config.database,
						config.username, config.maxIdleConns, config.maxOpenConns, config.connMaxLifetime);

				BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MysqlClientImpl.class);
				builder.setInitMethodName("init"); // 必须初始化

				builder.addPropertyValue("dataSource", initDataSource(config));
				if (config.showSql) {
					builder.addPropertyValue("showSql", true);
				}
				if (StringBase.isNotEmpty(config.packagesToScan)) {
					builder.addPropertyValue("packagesToScan", config.packagesToScan);
				}
				if (StringBase.isNotEmpty(config.configLocations)) {
					builder.addPropertyValue("configLocations", config.configLocations);
				} else {
					builder.addPropertyValue("configLocations", "classpath*:/query/**/*.xml");
				}
				registry.registerBeanDefinition(config.key, builder.getRawBeanDefinition());
			}
		}
	}

	private DataSource initDataSource(MysqlConfig config) {

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

		return ds;
	}

}
