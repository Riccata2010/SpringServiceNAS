package com.mio.aop.utils;

public class Escaper {

	public static String unescapeURLWith0(String mask) {
		return unescapeURL(mask).replace("*", "0");
	}

	public static String unescapeURL(String mask) {
		return mask.replace("%2A", "*").replace("%3A", ":").replace("%2B", "+").replace("%2D", "-");
	}

	public static String escapeURL(String url) {
		return url.replace("*", "%2A").replace(":", "%3A").replace("+", "%2B").replace("-", "%2D");
	}
}
