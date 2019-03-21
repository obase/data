package com.github.obase.app;

public class ArgsTest {

	public static void main(String[] args) {
		Args ags = new Args();
		ags.defArg("conf", true, "conf.xml file path");
		ags.defArg("debug", false, "debug mode");

		ags.parse(new String[] { "-debug", "-conf", "xxx/conf.yml", "abc" }, 0);
		System.out.println(ags.values());

		System.out.println(ags.help(App.class));
	}

}
