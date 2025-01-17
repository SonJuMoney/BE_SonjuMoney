package com.hana4.sonjumoney.dto;

import lombok.Builder;

@Builder
public record AlarmResultDto(
	Boolean hasNext,
	Long page,
	AlarmContentDto content
) {
	public static AlarmResultDto of(Boolean hasNext, Long page, AlarmContentDto content) {
		return AlarmResultDto.builder()
			.hasNext(hasNext)
			.page(page)
			.content(content)
			.build();
	}
}
