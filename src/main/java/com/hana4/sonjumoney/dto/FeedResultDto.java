package com.hana4.sonjumoney.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record FeedResultDto(
	Boolean hasNext,
	Integer page,
	List<FeedContentDto> contents
) {
	public static FeedResultDto of(Boolean hasNext, Integer page, List<FeedContentDto> contents) {
		return FeedResultDto.builder()
			.hasNext(hasNext)
			.page(page)
			.contents(contents)
			.build();
	}
}
