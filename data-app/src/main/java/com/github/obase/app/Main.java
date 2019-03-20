package com.github.obase.app;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.obase.app.impl.ArgsImpl;
import com.github.obase.app.impl.DefaultContext;
import com.github.obase.base.ClassBase;

// 特定扫描com.seasun.jx3dc.app目录下面实现App接口的所有类.

public class Main {

	static final Logger logger = LogManager.getLogger(Main.class);
	static final String DEF_APP_PACK = "com.seasun.jx3dc.app"; // 默认package目录

	public static void main(String[] args) {

		String prcApp = null;
		String[] prcArgs = null;
		Class<?> prcCls = null;
		try {

			Set<Class<?>> prcClsSet = ClassBase.scanPackClass(loadAppPackPath());

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

		apps = System.getProperty("APP_PACK_PATH", System.getProperty("app_pack_path"));
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

}
