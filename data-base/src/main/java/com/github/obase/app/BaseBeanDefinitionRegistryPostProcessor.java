package com.github.obase.app;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.StringValueResolver;

import com.github.obase.base.StringBase;

public class BaseBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanNameAware, BeanFactoryAware {

	final boolean ignoreUnresolvablePlaceholder;
	String beanName;
	BeanFactory beanFactory;

	public BaseBeanDefinitionRegistryPostProcessor(boolean ignoreUnresolvablePlaceholder) {
		this.ignoreUnresolvablePlaceholder = ignoreUnresolvablePlaceholder;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	// 静态配置处理
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		final BasePropertyResolver propertyResolver = new BasePropertyResolver();
		propertyResolver.setPlaceholderPrefix(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX);
		propertyResolver.setPlaceholderSuffix(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX);
		propertyResolver.setValueSeparator(PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR);

		StringValueResolver valueResolver = new StringValueResolver() {
			@Override
			public String resolveStringValue(String strVal) {
				String resolved = ignoreUnresolvablePlaceholder ? propertyResolver.resolvePlaceholders(strVal) : propertyResolver.resolveRequiredPlaceholders(strVal);
				return StringBase.isEmpty(resolved) ? null : resolved.trim();
			}
		};

		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
		String[] beanNames = beanFactory.getBeanDefinitionNames();
		for (String curName : beanNames) {
			if (!(curName.equals(this.beanName) && beanFactory.equals(this.beanFactory))) {
				BeanDefinition bd = beanFactory.getBeanDefinition(curName);
				try {
					visitor.visitBeanDefinition(bd);
				} catch (Exception ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
				}
			}
		}

		beanFactory.resolveAliases(valueResolver);
		beanFactory.addEmbeddedValueResolver(valueResolver);

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

	}

}
