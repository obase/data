package com.github.obase.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.obase.SystemException;
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
				System.out.println("conf file not exist: " + confFile);
				System.exit(1);
			}
			if (!file.isFile()) {
				System.out.println("conf file is directory: " + confFile);
				System.exit(2);
			}
			ConfBase.reset(file);
		} else {
			ConfBase.reset();
		}

		Context appCtx = null;
		App appBean = null;
		ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(new String[] { "classpath:" + SPRING_XML_LOCATION }, false);
		try {

			springContext.addBeanFactoryPostProcessor(new BaseBeanDefinitionRegistryPostProcessor(false));
			for (Class<?> cls : scanBeanDefinitionRegistryPostProcessor()) {
				springContext.addBeanFactoryPostProcessor((BeanDefinitionRegistryPostProcessor) cls.newInstance());
			}
			springContext.refresh();

			Map<String, App> beans = springContext.getBeansOfType(App.class);
			if (beans.size() == 0) {
				System.out.println("can't found any app bean in spring context");
				System.exit(3);
			}

			String appName = null;
			if (args.length > 0 && args[0].charAt(0) != '-') {
				appName = args[0];
			}

			if (appName == null && beans.size() == 1) {
				appBean = beans.values().iterator().next();
			} else {
				for (Map.Entry<String, App> entry : beans.entrySet()) {
					String name = entry.getKey();
					App bean = entry.getValue();

					Class<?> type = bean.getClass();
					if (StringBase.equalsIgnoreCase(appName, name) || StringBase.equalsIgnoreCase(appName, type.getSimpleName()) || StringBase.equalsIgnoreCase(appName, type.getCanonicalName())) {
						appBean = bean;
						break;
					}
				}
			}

			if (appBean != null) {
				Exception ex = null;
				appCtx = new Context(springContext);
				try {
					appBean.declare(flags);
					System.exit(appBean.execute(appCtx, flags));
				} catch (Exception e) {
					logger.error("app execute failed", e);
					ex = e;
				} finally {
					appBean.destroy(appCtx, ex);
				}
				System.exit(4);
			} else {
				System.out.println("can't found any app bean in spring context");
				System.exit(3);
			}
		} catch (ReflectiveOperationException | IOException e1) {
			throw new SystemException(e1);
		} finally {
			if (springContext != null) {
				springContext.close();
			}
		}
		System.exit(5);
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

	private static Set<Class<?>> scanBeanDefinitionRegistryPostProcessor() throws IOException {

		Set<Class<?>> sets = new LinkedHashSet<Class<?>>();

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] rss = resolver.getResources("classpath*:/META-INF/*.bdrpp");
		if (rss != null && rss.length > 0) {
			StringBuilder sb = new StringBuilder(1024);
			for (Resource rs : rss) {
				sb.setLength(0);
				InputStream in = null;
				try {
					in = rs.getInputStream();
					if (in != null) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						for (String line = null; (line = reader.readLine()) != null;) {
							line = line.trim();
							try {
								Class<?> cls = ClassBase.forName(line);
								if (BeanDefinitionRegistryPostProcessor.class.isAssignableFrom(cls)) {
									sets.add(cls);
								}
							} catch (ClassNotFoundException e) {
								logger.error("class not found: {}", line);
							}
						}
					}
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}
		}

		return sets;
	}
}
