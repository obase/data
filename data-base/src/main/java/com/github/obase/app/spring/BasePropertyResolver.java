package com.github.obase.app.spring;

import org.springframework.core.env.AbstractPropertyResolver;

import com.github.obase.base.ConfBase;

final class BasePropertyResolver extends AbstractPropertyResolver {

	@Override
	public boolean containsProperty(String key) {
		Object val = ConfBase.get(key);
		if (val == null) {
			val = ConfBase.getSysCnf(key, null);
		}
		return val != null;
	}

	@Override
	public String getProperty(String key) {
		return ConfBase.getString(key, ConfBase.getSysCnf(key, null));
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		String value = getProperty(key);
		if (value != null) {
			value = resolveNestedPlaceholders(value);

			if (!getConversionService().canConvert(String.class, targetType)) {
				throw new IllegalArgumentException(
						String.format("Cannot convert value [%s] from source type [%s] to target type [%s]", value, String.class.getSimpleName(), targetType.getSimpleName()));
			}
			return getConversionService().convert(value, targetType);
		}
		return null;
	}

	@Override
	protected String getPropertyAsRawString(String key) {
		return getProperty(key);
	}

}
