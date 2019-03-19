package com.seasun.data.app;

public interface Args {

	Args defArg(char opt, String longOpt, boolean hasArg, String desc);

	boolean hasArg(String opt);

	boolean hasArg(char opt);

	String getArg(String opt);

	String getArg(char opt);

	String[] getArgs();
}
