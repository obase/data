package com.github.obase.app;

import org.springframework.context.ApplicationContext;

public class Context {

	final ApplicationContext springContext;

	protected Context(ApplicationContext springContext) {
		this.springContext = springContext;
	}

}
