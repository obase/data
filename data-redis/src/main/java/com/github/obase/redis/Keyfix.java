package com.github.obase.redis;

import java.util.List;

public interface Keyfix {

	String key(String orgKey);

	String[] keys(String... orgKeys);

	String[] keysvalues(String... keysvalues);

	String[] keys(int n, String... params);

	List<String> keys(List<String> orgs);
}
