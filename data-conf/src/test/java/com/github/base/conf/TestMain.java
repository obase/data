package com.github.base.conf;

import org.yaml.snakeyaml.Yaml;

public class TestMain {

	public static void main(String[] args) {
		Yaml yaml = new Yaml();
		Object ret = (Object) yaml.load(TestMain.class.getResourceAsStream("/test.yaml"));
		System.out.println(ret.getClass());
		System.out.println(ret);
	}

}
