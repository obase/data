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

public class RedisClientDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	static final Logger logger = LogManager.getLogger(RedisClientDefinitionRegistryPostProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		List<RedisConfig> configs = ConfBase.bindObjectList("redis", RedisConfig.class);
		if (configs.size() > 0) {
			for (RedisConfig config : configs) {

				logger.info("init redis bean: key={}, address={}, database={}, maxIdles={}, maxConns={}, initConns={}", config.key, config.address, config.database, config.maxIdles, config.maxConns,
						config.initConns);

				BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RedisClientFactoryBean.class);
				builder.addConstructorArgValue(config);

				registry.registerBeanDefinition(config.key, builder.getRawBeanDefinition());
			}
		}
	}

}
