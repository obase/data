package com.github.obase.base;

public interface ConstBase {

	String APP = "APP";
	String ENV = "ENV";
	String CONF_FILE = "CONF_FILE";
	String CONF_NAME = "conf.yml";
	String APP_DIR = "/data/apps/";
	String LOG_DIR = "/data/logs/";
	String APP_PACK_BASE = ConfBase.getSysCnf("APP_PACK_BASE", "com.seasun.jx3dc.app");

}
