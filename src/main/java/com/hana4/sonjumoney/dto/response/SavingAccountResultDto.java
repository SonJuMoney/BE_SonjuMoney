package com.hana4.sonjumoney.dto.response;

import java.util.List;

import com.hana4.sonjumoney.dto.SavingAccountContentDto;

import lombok.Builder;

@Builder
public record SavingAccountResultDto(
	Boolean hasNext,
	Integer page,
	List<SavingAccountContentDto> contents
) {
	public static SavingAccountResultDto of(Boolean hasNext, Integer page, List<SavingAccountContentDto> contents) {
		return SavingAccountResultDto.builder()
			.hasNext(hasNext)
			.page(page)
			.contents(contents)
			.build();
	}
}
