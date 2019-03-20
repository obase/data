package com.github.base.conf;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.beans.BeanMap;
import org.yaml.snakeyaml.Yaml;

import com.github.obase.base.ClassBase;
import com.github.obase.base.StringBase;

public final class Config {

	static final Logger logger = LogManager.getLogger(Config.class);
	public static final String YAML_CONF = "YAML_CONF";
	public static final String YAML_FILE = "conf.yml";

	final Map<String, Object> data = new HashMap<String, Object>();

	private Config() {

		InputStream in = null;
		try {
			File file = null;

			// 1. 查询JVM属性及环境变量
			String path = System.getProperty(YAML_CONF, System.getProperty(YAML_CONF.toLowerCase(), System.getenv(YAML_CONF)));
			if (StringBase.isNotEmpty(path)) {
				file = new File(path);
			}
			// 2.查找类路径
			if (file == null || !file.exists()) {
				file = new File(ClassBase.cwd(), YAML_FILE);
			}
			// 3.上下文加载
			if (file != null && !file.exists()) {
				in = new BufferedInputStream(new FileInputStream(file));
			} else {
				in = ClassBase.getResourceAsStream("/" + YAML_FILE);
			}
			if (in != null) {
				Yaml y = new Yaml();
				Map<String, Object> ret = y.load(in);
				if (ret.size() > 0) {
					data.putAll(ret);
				}
			}

		} catch (Exception e) {
			logger.error("initial config failed", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
		}
	}

	// 延迟初始化
	static final Config Singleton = new Config();

	public static Map<String, Object> get() {
		return Singleton.data;
	}

	public static <T> T bind(T obj) {
		BeanMap bm = BeanMap.create(obj);
		bm.putAll(Singleton.data);
		return obj;
	}

	@SuppressWarnings("rawtypes")
	public static Object get(String xpath) {
		if (xpath == null) {
			return null;
		}

		Object val = Singleton.data;
		String[] steps = xpath.split("\\.");
		for (int i = 0; val != null && i < steps.length; i++) {
			if (val instanceof Map) {
				Map mval = (Map) val;
				val = mval.get(steps[i]);
			} else if (val instanceof List) {
				List lval = (List) val;
				Integer iv = StringBase.toInteger(steps[i]);
				if (iv != null && iv.intValue() < lval.size()) {
					val = lval.get(iv);
				} else {
					val = null;
				}
			} else if (val.getClass().isArray()) {
				Object[] arr = (Object[]) val;
				Integer iv = StringBase.toInteger(steps[i]);
				if (iv != null && iv.intValue() < arr.length) {
					val = arr[iv];
				} else {
					val = null;
				}
			} else {
				val = null;
			}
		}

		return val;
	}

	@SuppressWarnings("rawtypes")
	public static <T> T bind(String xpath, T obj) {
		Object val = get(xpath);
		if (val instanceof Map) {
			Map mval = (Map) val;
			BeanMap bm = BeanMap.create(obj);
			bm.putAll(mval);
			return obj;
		}
		return null;
	}

}
