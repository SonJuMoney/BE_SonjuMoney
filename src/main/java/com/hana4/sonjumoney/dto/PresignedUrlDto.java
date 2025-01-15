package com.hana4.sonjumoney.dto;

import lombok.Builder;

@Builder
public record PresignedUrlDto(
	String presignedUrl,
	String key
) {
	public static PresignedUrlDto of(
		String presignedUrl,
		String  key
	) {
		return PresignedUrlDto.builder()
			.presignedUrl(presignedUrl)
			.key(key)
			.build();
	}
	
}
