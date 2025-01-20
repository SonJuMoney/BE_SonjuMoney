package com.hana4.sonjumoney.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record AlarmResultDto(
	Boolean hasNext,
	Integer page,
	List<AlarmContentDto> contents
) {
	public static AlarmResultDto of(Boolean hasNext, Integer page, List<AlarmContentDto> contents) {
		return AlarmResultDto.builder()
			.hasNext(hasNext)
			.page(page)
			.contents(contents)
			.build();
	}
}
