package com.github.obase.test;

import java.io.File;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public abstract class BaseJUnitTester {

	public static final String ENV_PROPERTIES = "env.properties";
	public static final String CWD = new File("").getAbsolutePath();

	@ClassRule
	public static final EnvironmentVariables envs = new EnvironmentVariables();

	@BeforeClass
	public static void processSystemEnvironment() {
		loadSystemEnvironment(new File(CWD, ENV_PROPERTIES));
	}

	public static void loadSystemEnvironment(File envFile) {

		if (!envFile.exists()) {
			return;
		}

		try {
			Properties envProps = PropertiesLoaderUtils.loadProperties(new FileSystemResource(envFile));
			for (String name : envProps.stringPropertyNames()) {
				envs.set(name, envProps.getProperty(name));
			}
		} catch (Exception e) {
			System.err.println("Load system environment faild: " + envFile.getAbsolutePath() + ", error: " + e.getMessage());
		}
	}

	public static boolean isEmpty(String val) {
		return val == null || val.length() == 0;
	}

	public static boolean isNotEmpty(String val) {
		return val != null && val.length() > 0;
	}
}
