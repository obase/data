package com.github.base.conf;

import java.io.File;

public class TestMain {

	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getContextClassLoader().getResource("."));
		System.out.println(new File("./").getAbsolutePath());
	}

}
