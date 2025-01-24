package com.hana4.sonjumoney.dto;

import com.hana4.sonjumoney.domain.enums.ContentType;

import lombok.Builder;

@Builder
public record FeedContentContentDto(
	String url,
	ContentType content_type
) {
	public static FeedContentContentDto of(String url, ContentType content_type) {
		return FeedContentContentDto.builder()
			.url(url)
			.content_type(content_type)
			.build();
	}
}
