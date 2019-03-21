package com.github.obase.app;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.obase.base.ClassBase;

public class Main {

	static final Logger logger = LogManager.getLogger(Main.class);

	public static void main(String[] args) {

		for (String arg : args) {
			if ("-help".equalsIgnoreCase(arg)) {
				printAppArgsHelp();
				return;
			}
		}

		//		String appName = null;
		//		String[] appArgs = null;
		//
		//		// 分析第一个参数是否选项
		//		if (args.length > 0 && args[0].charAt(0) != '-') {
		//			appName = args[0];
		//			appArgs = new String[args.length - 1];
		//			System.arraycopy(args, 1, appArgs, 0, appArgs.length);
		//		} else {
		//			appArgs = args;
		//		}
		//
		//		// 注解要分二次,第一次抽取main所需的选项. 第二次抽取app需要的选项
		//
		//		// 创建上下文,但不refresh
		//		ClassPathXmlApplicationContext appctx = new ClassPathXmlApplicationContext(new String[] { "classpath:spring.xml" }, false);
		//
		//		// 动态注册需要的组件
		//
		//		// 动态刷新
		//		appctx.refresh();
	}

	private static void printAppArgsHelp() {
		try {
			Set<String> packs = new HashSet<String>(1);
			packs.add("/");
			Set<Class<?>> appClsSet = ClassBase.scanPackClass(packs, App.class);
			for (Class<?> cls : appClsSet) {
				App app = (App) cls.newInstance();
				Args args = new Args();
				app.declare(args);
				System.out.println(args.help(cls));
				System.out.println();
			}
		} catch (Exception e) {
			logger.error("print app args help failed", e);
		}
	}

}
