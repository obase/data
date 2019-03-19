package com.seasun.data.app;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seasun.data.app.impl.ArgsImpl;
import com.seasun.data.app.impl.DefaultContext;

// 特定扫描com.seasun.jx3dc.app目录下面实现App接口的所有类.

public class Main {

	static final Logger logger = LogManager.getLogger(Main.class);
	static final String APP_PACK_PATH = Main.class.getPackage().getName().replace('.', '/');

	public static void main(String[] args) {

		String prcApp = null;
		String[] prcArgs = null;
		Class<?> prcCls = null;
		try {
			Set<Class<?>> prcClsSet = scanProcessClass(APP_PACK_PATH);

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

	public static Set<Class<?>> scanProcessClass(String path) throws IOException {

		Set<String> cpathSet = new HashSet<String>();

		URL url = Main.class.getResource('/' + path); // 作为根目录扫描
		if ("jar".equals(url.getProtocol())) {
			// jar
			String surl = url.toString();
			int start = surl.indexOf(":");
			int end = surl.lastIndexOf("!/");
			scanProcessClasshInJar(cpathSet, new File(new URL(surl.substring(start + 1, end)).getFile()));
		} else {
			// file
			scanProcessClasshInFile(cpathSet, new File(url.getFile()));
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

	public static void scanProcessClasshInJar(Set<String> ret, File jarf) throws IOException {
		JarFile jf = null;
		try {
			jf = new JarFile(jarf);
			for (Enumeration<JarEntry> en = jf.entries(); en.hasMoreElements();) {
				String name = en.nextElement().getName();
				if (name.startsWith(APP_PACK_PATH) && name.endsWith(".class")) {
					ret.add(name);
				}
			}
		} finally {
			jf.close();
		}
	}

	public static void scanProcessClasshInFile(Set<String> ret, File dir) throws IOException {
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".class")) {
				ret.add(APP_PACK_PATH + "/" + file.getName());
			} else if (file.isDirectory()) {
				scanProcessClasshInFile(ret, file);
			}
		}
	}
}
