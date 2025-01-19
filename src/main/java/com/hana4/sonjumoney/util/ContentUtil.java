package com.hana4.sonjumoney.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;

public class ContentUtil {

	public static String getExtension(String fileName) {
		String[] parts = fileName.split("\\.");
		return parts[parts.length - 1];
	}

	public static String getFileNameFromUrl(String url) {
		if (url == null || url.isEmpty()) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		Pattern pattern = Pattern.compile(".*/([^/]+/[^/]+/[^/]+)$");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
	}
}
