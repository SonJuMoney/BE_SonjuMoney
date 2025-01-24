package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record AlarmStatusResponse(
	@JsonProperty(value = "is_exist")
	Boolean isExist
) {
	public static AlarmStatusResponse of(Boolean isExist) {
		return AlarmStatusResponse.builder()
			.isExist(isExist)
			.build();
	}
}
