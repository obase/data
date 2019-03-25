package com.github.obase.base;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.beans.BeanMap;

public class ObjectBase {

	static final Logger logger = LogManager.getLogger(ObjectBase.class);

	static final ConcurrentLinkedQueue<AutoCloseable> CLOSES = new ConcurrentLinkedQueue<AutoCloseable>();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for (AutoCloseable c : CLOSES) {
					try {
						c.close();
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}
		});
	}

	public static void closeWhenShutdown(AutoCloseable c) {
		CLOSES.add(c);
	}

	@SuppressWarnings("rawtypes")
	public static <T> T convert(Map map, T bean) {
		BeanMap bm = BeanMap.create(bean);
		bm.putAll(map);
		return bean;
	}

	@SuppressWarnings("rawtypes")
	public static <T> T convert(Map map, Class<T> type) throws ReflectiveOperationException {
		return convert(map, type.newInstance());
	}

	public static <T> Set<T> asSet(@SuppressWarnings("unchecked") T... args) {
		Set<T> sets = new LinkedHashSet<T>();
		Collections.addAll(sets, args);
		return sets;
	}

	public static <T> boolean isEmpty(Collection<T> c) {
		return c == null || c.size() == 0;
	}

	public static <T> boolean isNotEmpty(Collection<T> c) {
		return c != null && c.size() > 0;
	}

	public static <K, V> boolean isEmpty(Map<K, V> m) {
		return m == null || m.size() == 0;
	}

	public static <K, V> boolean isNotEmpty(Map<K, V> m) {
		return m != null && m.size() > 0;
	}
}
