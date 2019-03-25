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

public class MongoClientDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	static final Logger logger = LogManager.getLogger(MongoClientDefinitionRegistryPostProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// nothing
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

		List<MongoConfig> configs = ConfBase.bindObjectList("mongo", MongoConfig.class);
		if (configs.size() > 0) {
			for (MongoConfig config : configs) {

				logger.info("init mongo bean: key={}, address={}, database={}, username={}, authSource={}", config.key, config.address, config.database, config.username, config.authSource);

				BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MongoClientFactoryBean.class);
				builder.addConstructorArgValue(config);
				registry.registerBeanDefinition(config.key, builder.getRawBeanDefinition());
			}
		}
	}

}
