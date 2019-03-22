package com.github.obase.app;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.obase.app.spring.BaseBeanDefinitionRegistryPostProcessor;
import com.github.obase.base.ClassBase;
import com.github.obase.base.ConfBase;
import com.github.obase.base.SaxBase;
import com.github.obase.base.StringBase;

public class Main {

	static final Logger logger = LogManager.getLogger(Main.class);

	static final String SPRING_XML_LOCATION = "spring.xml";

	public static void main(String[] args) {

		// 帮助索引
		for (String arg : args) {
			if ("-help".equalsIgnoreCase(arg) || "-h".equalsIgnoreCase(arg)) {
				printAppArgsHelp(System.out, parseBasePack(SPRING_XML_LOCATION));
				System.exit(0);
			}
		}

		// 先解析配置文件, ConfBase优先上下文初始化
		Flags flags = new Flags();
		flags.defArg("conf", true, "the conf file conf.xml path. Default is /data/apps/$APP/conf.xml[.$ENV]");
		flags.parse(args);

		String confFile = flags.getArg("conf");
		if (StringBase.isNotEmpty(confFile)) {
			// 通过系统属性传值给ConfBase解析conf.xml
			File file = new File(confFile);
			if (!file.exists()) {
				System.out.println("conf file can't found: " + confFile);
				System.exit(1);
			}
			ConfBase.reset(file);
		} else {
			ConfBase.reset();
		}

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "classpath:" + SPRING_XML_LOCATION }, false);
		try {
			ctx.addBeanFactoryPostProcessor(new BaseBeanDefinitionRegistryPostProcessor(false));
			ctx.refresh();
			ctx.close();
			ctx = null;
			System.exit(0);
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
		System.exit(1);
	}

	private static Set<String> parseBasePack(String springXmlPath) {
		Set<String> result = new LinkedHashSet<String>();
		SaxBase.parse(ClassBase.getResourceAsStream(springXmlPath), new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if ("component-scan".equalsIgnoreCase(localName)) {
					String packs = attributes.getValue("base-package");
					Collections.addAll(result, packs.split("\\s*,\\s*"));
				}
			}
		});
		return result;
	}

	private static void printAppArgsHelp(Appendable out, Set<String> packs) {
		try {
			Set<Class<?>> appClsSet = ClassBase.scanPackClass(packs, App.class);
			for (Class<?> cls : appClsSet) {
				App app = (App) cls.newInstance();
				Flags args = new Flags();
				app.declare(args);
				out.append(args.help(cls)).append("\n");
			}
		} catch (Exception e) {
			logger.error("print app args help failed", e);
		}
	}

}
