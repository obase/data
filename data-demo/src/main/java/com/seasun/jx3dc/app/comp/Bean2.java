package com.seasun.jx3dc.app.comp;

import org.springframework.beans.factory.annotation.Autowired;

public class Bean2 {

	@Autowired
	public Bean1 bean;

	public Bean1 getBean() {
		return bean;
	}

	public void setBean(Bean1 bean) {
		this.bean = bean;
	}

}
