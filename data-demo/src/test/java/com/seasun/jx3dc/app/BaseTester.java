package com.seasun.jx3dc.app;

import java.io.IOException;

import com.github.obase.app.Main;

public class BaseTester {

	public static void main(String[] args) throws InterruptedException, IOException {
		//		Main.main(new String[] {});
		//		URL url = ClassBase.getResource("/spring.xml");//ClassBase.getResourceAsStream("/spring.xml");
		//		System.out.println(url);
		//		InputStream in = ClassBase.getResourceAsStream("/spring.xml");
		//		System.out.println(in == null);
		//		if (in != null) {
		//			in.close();
		//		}

		TestInf inf = new TestInf() {
		};
		System.out.println(inf.info());
		System.out.println(TestInf.version());
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
