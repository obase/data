package com.github.obase.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.beans.BeanMap;
import org.yaml.snakeyaml.Yaml;

/**
 * # 配置读取顺序:
 * - BASE_CONF系统属性或环境变量,一般由-c/--conf指定,然后保存于System.properties中. 也可由环境变量指定
 * - ClassLoader.getResource("/conf.yml.$ENV")读取类路径下面. 默认代码.gitignore会忽略所有*.yml文件
 * - 如果有$APP存在,则读取/data/apps/$APP/conf.yml.$ENV
 */
public class ConfBase implements ConstBase {

	static final Logger logger = LogManager.getLogger(ConfBase.class);

	final Map<String, Object> data = new HashMap<String, Object>();

	ConfBase() {
		InputStream in = null;
		String confFile = System.getProperty(CONF_FILE, System.getProperty(CONF_FILE.toLowerCase(), System.getenv(CONF_FILE)));
		try {
			if (StringBase.isNotEmpty(confFile)) {
				in = new FileInputStream(confFile);
			} else {
				String confName = CONF_NAME;
				String env = System.getProperty(ENV, System.getProperty(ENV.toLowerCase(), System.getenv(ENV)));
				if (StringBase.isNotEmpty(env)) {
					confName += "." + env;
				}
				in = ClassBase.getResourceAsStream(confName);
				if (in == null && !CONF_NAME.equals(confName)) {
					in = ClassBase.getResourceAsStream(CONF_NAME);
				}
				if (in == null) {
					String app = System.getProperty(APP, System.getProperty(APP.toLowerCase(), System.getenv(APP)));
					if (StringBase.isNotEmpty(app)) {
						File file = new File(APP_DIR + app + "/" + confName);
						if (!file.exists() && !CONF_NAME.equals(confName)) {
							file = new File(APP_DIR + app + "/" + CONF_NAME);
						}
						if (file.exists()) {
							in = new FileInputStream(file);
						}
					}
				}
			}

			if (in != null) {
				Yaml y = new Yaml();
				Map<String, Object> ret = y.load(new BufferedInputStream(in));
				if (ret.size() > 0) {
					data.putAll(ret);
				}
			}

		} catch (FileNotFoundException e) {
			logger.error("file not found: " + confFile);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("close inputstream failed", e);
				}
			}
		}

	}

	// 延迟初始化
	static final ConfBase Singleton = new ConfBase();

	public static Map<String, Object> get() {
		return Singleton.data;
	}

	/**
	 * 要求obj满足JavaBean规范,即属性有getter/setter方法
	 */
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

	/**
	 * 要求obj满足JavaBean规范,即属性有getter/setter方法
	 */
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

	public static Integer getInteger(String xpath, Integer def) {
		Object val = get(xpath);
		if (val instanceof Number) {
			return ((Number) val).intValue();
		} else if (val instanceof String) {
			return StringBase.toInteger((String) val);
		}
		return def;
	}

	public static String getString(String xpath, String def) {
		Object val = get(xpath);
		if (val instanceof Number) {
			return val.toString();
		} else if (val instanceof String) {
			return (String) val;
		}
		return def;
	}

	public static Boolean getBoolean(String xpath, Boolean def) {
		Object val = get(xpath);
		if (val instanceof Boolean) {
			return (Boolean) val;
		}
		return def;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(String xpath, List<T> def) {
		Object val = get(xpath);
		if (val instanceof List) {
			return (List<T>) val;
		}
		return def;
	}

	@SuppressWarnings("rawtypes")
	public static String[] getStringArray(String xpath, String[] def) {
		Object val = get(xpath);
		if (val instanceof String) {
			String sval = (String) val;
			return sval.split("\\s*,\\s*");
		} else if (val instanceof List) {
			List lval = (List) val;
			int size = lval.size();
			String[] ret = new String[size];
			for (int i = 0; i < size; i++) {
				ret[i] = lval.get(i).toString();
			}
			return ret;
		}
		return def;
	}
}
