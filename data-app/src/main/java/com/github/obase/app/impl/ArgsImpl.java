package com.github.obase.app.impl;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.obase.app.Args;

public class ArgsImpl implements Args {

	public Options options;
	String[] args;
	CommandLine line;

	@Override
	public Args defArg(char opt, String longOpt, boolean hasArg, String desc) {
		if (options == null) {
			options = new Options();
		}
		options.addOption(String.valueOf(opt), longOpt, hasArg, desc);
		return this;
	}

	@Override
	public boolean hasArg(String opt) {
		if (line == null) {
			return false;
		}
		return line.hasOption(opt);
	}

	@Override
	public boolean hasArg(char opt) {
		if (line == null) {
			return false;
		}
		return line.hasOption(opt);
	}

	@Override
	public String getArg(String opt) {
		if (line == null) {
			return null;
		}
		return line.getOptionValue(opt);
	}

	@Override
	public String getArg(char opt) {
		if (line == null) {
			return null;
		}
		return line.getOptionValue(opt);
	}

	@Override
	public String[] getArgs() {
		return args;
	}

	public void parse(String[] args) throws ParseException {
		this.args = args;
		if (options != null) {
			line = new DefaultParser().parse(options, args);
		}
	}

	public void help(PrintWriter pw) {
		if (pw == null) {
			new HelpFormatter().printHelp("java -jar <jarfile> [COMMAND] [OPTIONS]", options);
		} else {
			new HelpFormatter().printHelp(pw, 200, "java -jar <jarfile> [COMMAND] [OPTIONS]", "", options, 2, 0, "");
		}
	}
}
