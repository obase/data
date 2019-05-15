package com.github.obase.app;

import java.util.LinkedHashMap;
import java.util.Map;

public class Flags {

	static class Flag {
		final String name; // flag name
		final boolean value; // has value
		final String desc; // description

		Flag(String name, boolean value, String desc) {
			this.name = name;
			this.value = value;
			this.desc = desc;
		}
	}

	String[] args;
	final Map<String, Flag> flags = new LinkedHashMap<String, Flag>();
	final Map<String, String> values = new LinkedHashMap<String, String>();

	public void defArg(String name, boolean value, String desc) {
		flags.put("-" + name, new Flag(name, value, desc));
	}

	public boolean hasArg(String opt) {
		return values.containsKey(opt);
	}

	public String getArg(String opt) {
		return values.get(opt);
	}

	public String getArg(String opt, String def) {
		String val = values.get(opt);
		return val == null ? def : val;
	}

	public String[] args() {
		return args;
	}

	public void parse(String[] args) {
		this.args = args;
		for (int i = 0; i < args.length;) {
			if (args[i].charAt(0) == '-') {
				int j = i + 1;
				if (j < args.length && args[j].charAt(0) != '-') {
					this.values.put(args[i].substring(1), args[j]);
					i = j + 1;
				} else {
					this.values.put(args[i].substring(1), null);
					i = j;
				}
			} else {
				i++;
			}
		}
	}

	public String help(Class<?> clazz) {
		StringBuilder sb = new StringBuilder(1024);

		sb.append(String.format("Usage: %s | %s\n", clazz.getSimpleName(), clazz.getCanonicalName()));
		for (Map.Entry<String, Flag> entry : flags.entrySet()) {
			String key = entry.getKey();
			Flag val = entry.getValue();
			sb.append(String.format("\u0020\u0020%-20s %-10s\n", key + (val.value ? " <VALUE>" : " "), val.desc));
		}
		return sb.toString();
	}

	public Map<String, String> values() {
		return this.values;
	}
}
