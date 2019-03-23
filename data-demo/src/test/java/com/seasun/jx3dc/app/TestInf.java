package com.seasun.jx3dc.app;

public interface TestInf {

	static String version() {
		return "x.y.z";
	}

	default String info() {
		return "information here";
	}

}
