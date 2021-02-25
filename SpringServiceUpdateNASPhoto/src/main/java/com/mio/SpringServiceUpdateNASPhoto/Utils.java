package com.mio.SpringServiceUpdateNASPhoto;

import java.io.File;

import com.mio.SpringCustomAspectBaseLib.BaseUtils;

public class Utils extends BaseUtils {

	public static File getOldUniquePath(File data) {
		int n = 0;
		File old = null;
		while ((old = new File(data.toString() + ".old." + (++n))).exists()) {
		}
		return old;
	}

	public static String getExtensionFileName(File file) {
		return getExtensionFileName((file != null) ? file.getName() : null);
	}

	public static String getExtensionFileName(String name) {
		String ext = null;
		if (name != null && name.length() > 0) {
			int i = name.lastIndexOf(".");
			if (i > -1) {
				ext = name.substring(i).toLowerCase().trim();
			}
		}
		return ext;
	}

	public static void main(String[] args) {

	}
}
