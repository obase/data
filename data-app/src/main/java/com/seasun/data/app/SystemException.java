package com.seasun.data.app;

public class SystemException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static final int UNKNOWN = -1;

	public final int code;
	public final String msg;
	public final String tag;

	public SystemException(int code, String msg, String tag) {
		this.code = code;
		this.msg = msg;
		this.tag = tag;
	}

	public SystemException(String msg) {
		super(msg);
		this.code = UNKNOWN;
		this.msg = msg;
		this.tag = null;
	}

	public SystemException(Throwable t) {
		super(t);
		if (t instanceof SystemException) {
			SystemException ae = (SystemException) t;
			this.code = ae.code;
			this.msg = ae.msg;
			this.tag = ae.tag;
		} else {
			this.code = UNKNOWN;
			this.msg = t.getMessage();
			this.tag = null;
		}
	}

}
