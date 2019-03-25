package com.github.obase.beans;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import com.github.obase.base.ConfBase;

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

				BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MysqlClientFactoryBean.class);
				builder.addConstructorArgValue(config);

				registry.registerBeanDefinition(config.key, builder.getRawBeanDefinition());
			}
		}
	}

}
