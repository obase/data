package com.github.obase.app;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.obase.app.impl.ArgsImpl;
import com.github.obase.app.impl.DefaultContext;

// 特定扫描com.seasun.jx3dc.app目录下面实现App接口的所有类.

public class Main {

	static final Logger logger = LogManager.getLogger(Main.class);
	static final String DEF_APP_PACK = "com.seasun.jx3dc.app"; // 默认package目录

	public static void main(String[] args) {

		String prcApp = null;
		String[] prcArgs = null;
		Class<?> prcCls = null;
		try {

			Set<Class<?>> prcClsSet = scanPackClass(loadAppPackPath());

			if (args.length > 0 && args[0].charAt(0) != '-') {
				prcApp = args[0];
				prcArgs = new String[args.length - 1];
				System.arraycopy(args, 1, prcArgs, 0, prcArgs.length);
			} else {
				prcApp = null;
				prcArgs = args;
			}

			int size = prcClsSet.size();
			if (prcApp == null) {

				if (size == 0) {
					System.err.println("can't found any process class in classpath");
				} else if (size == 1) {
					prcCls = prcClsSet.iterator().next();
				} else {
					System.err.println("found more than one process class in classpath: " + prcClsSet);
				}

			} else {
				_OUTER_: {
					for (Class<?> cls : prcClsSet) {
						// 短名与长名都可以匹配得到
						if (cls.getSimpleName().equalsIgnoreCase(prcApp) || cls.getCanonicalName().equalsIgnoreCase(prcApp)) {
							prcCls = cls;
							break _OUTER_;
						}
					}
					System.err.println("can't found corresponding process class in classpath");
				}
			}
		} catch (IOException e) {
			logger.error("scan process class in classpath failed", e);
		}

		if (prcCls != null) {
			try {
				App prcObj = (App) prcCls.newInstance();
				System.exit(process(prcObj, prcArgs));
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("create process failed: " + prcCls.getCanonicalName(), e);
			}
		}
		System.exit(1);
	}

	public static Set<String> loadAppPackPath() {
		Set<String> packs = new LinkedHashSet<String>();

		String apps = System.getenv("APP_PACK_PATH");
		if (apps != null && apps.length() > 0) {
			String[] vs = apps.split("\\s*,\\s*");
			for (String v : vs) {
				packs.add(v);
			}
		}

		apps = System.getProperty("APP_PACK_PATH");
		if (apps != null && apps.length() > 0) {
			String[] vs = apps.split("\\s*,\\s*");
			for (String v : vs) {
				packs.add(v);
			}
		}

		if (packs.size() == 0) {
			packs.add(DEF_APP_PACK);
		}

		return packs;
	}

	public static int process(App prcObj, String[] prcArgs) {

		ArgsImpl args = new ArgsImpl();
		try {
			prcObj.declare(args);
			args.parse(prcArgs);
		} catch (Exception e) {
			logger.error("process declare failed", e);
			System.err.println("process failed: " + e.getMessage());
			return 1;
		}

		if (args.hasArg('h') || args.hasArg("help")) {
			args.help(null);
			return 0;
		}

		Context ctx = null;
		try {
			ctx = new DefaultContext();
			ctx.init();
		} catch (Exception e) {
			logger.error("create context failed", e);
			if (ctx != null) {
				ctx.destroy();
			}
			return 1;
		}

		Exception ex = null;
		try {
			prcObj.execute(ctx, args);
		} catch (Exception e) {
			logger.error("process execute failed", e);
			ex = e;
		} finally {
			prcObj.destroy(ctx, ex);
			ctx.destroy();
		}

		return 0;
	}

	public static void scanPackClassInJar(Set<String> ret, File jarf, String[] prefixs) throws IOException {
		JarFile jfile = null;
		try {
			jfile = new JarFile(jarf);
			for (Enumeration<JarEntry> enums = jfile.entries(); enums.hasMoreElements();) {
				String name = enums.nextElement().getName();
				for (String pre : prefixs) {
					if (name.startsWith(pre) && name.endsWith(".class")) {
						ret.add(name);
						break;
					}
				}
			}
		} finally {
			if (jfile != null) {
				try {
					jfile.close();
				} catch (IOException e) {
					logger.error("close jar file failed", e);
				}
			}
		}
	}

	public static void scanPackClassInFile(Set<String> ret, File dir, String prefix) throws IOException {
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".class")) {
				ret.add(prefix + "/" + file.getName());
			} else if (file.isDirectory()) {
				scanPackClassInFile(ret, file, prefix);
			}
		}
	}

	public static Set<Class<?>> scanPackClass(Set<String> packs) throws IOException {

		if (packs.size() == 0) {
			return Collections.emptySet();
		}

		Set<String> cpathSet = new LinkedHashSet<String>();

		String[] prefixs = new String[packs.size()];
		int idx = 0;
		for (String pack : packs) {
			prefixs[idx++] = pack.replace('.', '/');
		}

		String[] clsspaths;
		if (File.separatorChar == '\\') {
			// Windows
			clsspaths = System.getProperty("java.class.path", "").split("\\;");
		} else {
			// Linux/Unix
			clsspaths = System.getProperty("java.class.path", "").split("\\:");
		}

		for (String clsspath : clsspaths) {
			File file = new File(clsspath);
			if (file.isFile()) {
				// jar
				scanPackClassInJar(cpathSet, file, prefixs);
			} else {
				// dir
				for (String prefix : prefixs) {
					File dir = new File(file, prefix);
					if (dir.exists() && dir.isDirectory()) {
						scanPackClassInFile(cpathSet, dir, prefix);
					}
				}
			}
		}

		Set<Class<?>> clazzSet = new HashSet<Class<?>>(cpathSet.size());
		for (String cpath : cpathSet) {
			try {
				int end = cpath.lastIndexOf(".class");
				if (end == -1) {
					end = cpath.length();
				}
				Class<?> clazz = Class.forName(cpath.substring(0, end).replace('/', '.'));
				if (App.class.isAssignableFrom(clazz) && App.class != clazz) { // 必须实现APP及非abstract
					int mfs = clazz.getModifiers();
					if (Modifier.isPublic(mfs) && !Modifier.isAbstract(mfs)) {
						clazzSet.add(clazz);
					}
				}
			} catch (ClassNotFoundException e) {
				// skip the unkonw class
				logger.error("class not found: %s", cpath);
			}
		}
		return clazzSet;
	}
}
