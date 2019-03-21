package com.github.obase.base;

public class StringBase {

	public static boolean isEmpty(String val) {
		return val == null || val.length() == 0;
	}

	public static boolean isBlank(String val) {
		if (val == null || val.length() == 0) {
			return true;
		}
		for (int i = 0, ln = val.length(); i < ln; i++) {
			if (!Character.isWhitespace(val.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(String val) {
		return val != null && val.length() != 0;
	}

	public static boolean isNotBlank(String val) {
		if (val == null || val.length() == 0) {
			return false;
		}
		for (int i = 0, ln = val.length(); i < ln; i++) {
			if (!Character.isWhitespace(val.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInteger(String str) {

		if (str == null) {
			return false;
		}

		for (int ln = str.length() - 1; ln >= 0; ln--) {
			char ch = str.charAt(ln);
			if (ch < '0' || ch > '9') {
				return false;
			}
		}
		return true;
	}

	public static Integer toInteger(String str) {
		if (str == null) {
			return null;
		}
		// long
		int ret = 0;
		for (int i = 0, size = str.length(); i < size; i++) {
			char ch = str.charAt(i);
			if (ch < '0' || ch > '9') {
				return null;
			}
			ret = ret * 10 + ch - '0';
		}
		return ret;
	}

	public static Long toLong(String str) {
		if (str == null) {
			return null;
		}
		// long
		long ret = 0;
		for (int i = 0, size = str.length(); i < size; i++) {
			char ch = str.charAt(i);
			if (ch < '0' || ch > '9') {
				return null;
			}
			ret = ret * 10 + ch - '0';
		}
		return ret;

	}

	public static String nvl(String v1, String v2) {
		return v1 != null ? v1 : v2;
	}
}
