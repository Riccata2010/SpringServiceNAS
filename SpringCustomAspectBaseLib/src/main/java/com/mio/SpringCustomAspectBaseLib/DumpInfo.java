package com.mio.SpringCustomAspectBaseLib;

public class DumpInfo {

	public static void dump() {
		System.out.println("=================================================");
		System.getProperties().forEach((k, v) -> {
			System.out.println(k + "=" + v);
		});
		System.out.println("=================================================");
	}
}
