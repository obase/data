package com.github.obase.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.beans.BeanMap;
import org.yaml.snakeyaml.Yaml;

import com.github.obase.SystemException;

/**
 * # 配置读取顺序:
 * - BASE_CONF系统属性或环境变量,一般由-c/--conf指定,然后保存于System.properties中. 也可由环境变量指定
 * - ClassLoader.getResource("/conf.yml.$ENV")读取类路径下面. 默认代码.gitignore会忽略所有*.yml文件
 * - 如果有$APP存在,则读取/data/apps/$APP/conf.yml.$ENV
 */
public class ConfBase implements ConstBase {

	static final Logger logger = LogManager.getLogger(ConfBase.class);

	final Map<String, Object> data = new HashMap<String, Object>();

	/**
	 * 加载规则:
	 * 1. 系统属性/环境变量: CONF_FILE
	 * 2. /data/apps/$APP/conf.yml.$ENV
	 * 3. classpath:conf.yml.$ENV
	 */
	public static ConfBase reset() {

		InputStream in = null;
		try {
			String confFile = getSysCnf(CONF_FILE, null);
			if (confFile != null) {
				File file = new File(confFile);
				if (file.exists() && file.isFile()) {
					in = new BufferedInputStream(new FileInputStream(file));
				}
			}
			if (in == null) {
				String confName = CONF_NAME;
				String env = getSysCnf(ENV, null);
				if (env != null) {
					confName += "." + env;
				}
				if (in == null) {
					String app = getSysCnf(APP, null);
					if (app != null) {
						File file = new File(APP_DIR + app, confName);
						if (file.exists() && file.isFile()) {
							in = new BufferedInputStream(new FileInputStream(file));
						}
					}
				}
				if (in == null) {
					in = ClassBase.getResourceAsStream(confName);
				}
			}
			if (in != null) {
				Yaml y = new Yaml();
				Map<String, Object> ret = y.load(new BufferedInputStream(in));
				if (ret.size() > 0) {
					Singleton.data.putAll(ret);
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("file not found: ", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("close inputstream failed", e);
				}
			}
		}
		return Singleton;
	}

	/**
	 * 明确加载路径
	 */
	public static ConfBase reset(File file) {

		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			if (in != null) {
				Yaml y = new Yaml();
				Map<String, Object> ret = y.load(new BufferedInputStream(in));
				if (ret.size() > 0) {
					Singleton.data.putAll(ret);
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("file not found: " + file);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("close inputstream failed", e);
				}
			}
		}
		return Singleton;
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
			return ObjectBase.convert((Map) val, obj);
		}
		return null;
	}

	public static Integer getInteger(String xpath, Integer def) {
		Object val = get(xpath);
		return toInteger(val, def);
	}

	public static Integer toInteger(Object val, Integer def) {
		if (val instanceof Number) {
			return ((Number) val).intValue();
		} else if (val instanceof String) {
			return StringBase.toInteger((String) val);
		}
		return def;
	}

	public static String getString(String xpath, String def) {
		Object val = get(xpath);
		return toString(val, def);
	}

	public static String toString(Object val, String def) {
		if (val instanceof Number) {
			return val.toString();
		} else if (val instanceof String) {
			return (String) val;
		}
		return def;
	}

	public static Boolean getBoolean(String xpath, Boolean def) {
		Object val = get(xpath);
		return toBoolean(val, def);
	}

	public static Boolean toBoolean(Object val, Boolean def) {
		if (val instanceof Boolean) {
			return (Boolean) val;
		} else if (val instanceof String) {
			return "true".equalsIgnoreCase((String) val);
		} else if (val instanceof Number) {
			return ((Number) val).intValue() != 0;
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
	public static <T> List<T> bindObjectList(String xpath, Class<T> type) {
		try {
			List<T> list = new LinkedList<T>();
			Object val = get(xpath);
			if (val instanceof List) {
				List lval = (List) val;
				for (Object v : lval) {
					if (v instanceof Map) {
						list.add(ObjectBase.convert((Map) v, type));
					} else {
						list.add(null);
					}
				}
			} else if (val instanceof Map) {
				list.add(ObjectBase.convert((Map) val, type));
			}
			return list;
		} catch (ReflectiveOperationException e) {
			throw new SystemException(e);
		}
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

	public static String getSysCnf(String name, String def) {
		String val = System.getProperty(name, System.getProperty(name.toLowerCase(), System.getenv(name)));
		if (val == null || val.length() == 0) {
			return def;
		}
		return val;
	}
}
