package com.hana4.sonjumoney.util;

public class ContentUtil {
	public static String getExtension(String fileName) {
		String[] parts = fileName.split("\\.");
		return parts[parts.length - 1];
	}
}
