package com.seasun.jx3dc.app;

import com.github.obase.base.ConfBase;

public class BaseTester {

	public static void main(String[] args) throws InterruptedException {
		//		System.out.println(BaseTester.class.getResource("/com/seasun/jx3dc/app"));
		//		System.out.println(System.getProperties());
		//		Main.main(new String[] { "-a" });
		//		System.setProperty(ConfBase.CONF_FILE, "E:\\jx3workspace\\conf.yml");
		System.out.println(ConfBase.getBoolean("server.debug", null));
	}

	static class Server {
		public String runmode;
		public int port;

		public String getRunmode() {
			return runmode;
		}

		public void setRunmode(String runmode) {
			this.runmode = runmode;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

	}
}
