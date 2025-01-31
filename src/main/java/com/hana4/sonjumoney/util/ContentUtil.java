package com.hana4.sonjumoney.util;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hana4.sonjumoney.domain.enums.ContentType;
import com.hana4.sonjumoney.domain.enums.FeedType;
import com.hana4.sonjumoney.dto.ContentPrefix;
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

	public static ContentType classifyContentType(String extension) {
		if (extension == null || extension.isEmpty()) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}

		final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "bmp", "tiff", "webp");
		final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm");

		if (IMAGE_EXTENSIONS.contains(extension)) {
			return ContentType.IMAGE;
		} else if (VIDEO_EXTENSIONS.contains(extension)) {
			return ContentType.VIDEO;
		} else {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}

	}

	public static ContentPrefix convertFeedTypeToContentPrefix(FeedType feedType) {
		switch (feedType) {
			case NORMAL -> {
				return ContentPrefix.FEED;
			}
			case ALLOWANCE -> {
				return ContentPrefix.ALLOWANCE;
			}
			case THANKS -> {
				return ContentPrefix.THANKS;
			}
			default -> throw new CommonException(ErrorCode.BAD_REQUEST);
		}
	}
}
