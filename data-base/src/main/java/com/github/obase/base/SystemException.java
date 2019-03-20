package com.github.obase.base;

public final class SystemException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static int CODE_UNKNOWN = -1;

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
		this.code = CODE_UNKNOWN;
		this.msg = msg;
		this.tag = null;
	}

	public SystemException(Throwable t) {
		super(t);
		this.code = CODE_UNKNOWN;
		this.msg = t.getMessage();
		this.tag = null;
	}

	public SystemException(String msg, Throwable t) {
		super(msg, t);
		this.code = CODE_UNKNOWN;
		this.msg = msg;
		this.tag = null;
	}

}
