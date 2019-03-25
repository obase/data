package com.seasun.jx3dc.app.comp;

import org.springframework.stereotype.Service;

import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.Table;

@Service
@Table(name="bean1")
public class Bean1 {
	
	@Column(name="name2")
	public String name;
	
	@Column(name="age")
	public int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
