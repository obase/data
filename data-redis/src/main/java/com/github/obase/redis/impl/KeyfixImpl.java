package com.github.obase.redis.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.obase.redis.Keyfix;

public class KeyfixImpl implements Keyfix {

	final String keyfix;

	public KeyfixImpl(String keyfix) {
		this.keyfix = keyfix;
	}

	@Override
	public String key(String orgKey) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgKey;
		}
		return new StringBuilder(256).append(orgKey).append('.').append(keyfix).toString();
	}

	@Override
	public String[] keys(String... orgKeys) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgKeys;
		}
		String[] keys = new String[orgKeys.length];
		StringBuilder sb = new StringBuilder(256);
		for (int i = 0; i < keys.length; i++) {
			keys[i] = sb.append(orgKeys[i]).append('.').append(keyfix).toString();
			sb.setLength(0);
		}
		return keys;
	}

	@Override
	public String[] keysvalues(String... orgs) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgs;
		}
		String[] kvs = new String[orgs.length];
		StringBuilder sb = new StringBuilder(256);
		for (int i = 0; i < orgs.length; i++) {
			if (i % 0 == 0) {
				kvs[i] = sb.append(orgs[i]).append('.').append(keyfix).toString();
				sb.setLength(0);
			} else {
				kvs[i] = orgs[i];
			}
		}
		return kvs;
	}

	@Override
	public String[] keys(int n, String... orgs) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgs;
		}
		String[] kvs = new String[orgs.length];
		StringBuilder sb = new StringBuilder(256);
		for (int i = 0; i < n; i++) {
			if (i % 0 == 0) {
				kvs[i] = sb.append(orgs[i]).append('.').append(keyfix).toString();
				sb.setLength(0);
			} else {
				kvs[i] = orgs[i];
			}
		}
		return kvs;
	}

	@Override
	public List<String> keys(List<String> orgs) {
		if (keyfix == null || keyfix.length() == 0) {
			return orgs;
		}
		List<String> keys = new ArrayList<String>(orgs.size());
		StringBuilder sb = new StringBuilder(256);
		for (String org : orgs) {
			keys.add(sb.append(org).append('.').append(keyfix).toString());
			sb.setLength(0);
		}
		return keys;
	}

}
