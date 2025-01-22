package com.hana4.sonjumoney.dto.response;

import com.hana4.sonjumoney.dto.SavingAccountResultDto;

import lombok.Builder;

@Builder
public record GetSavingAccountResponse(
	Boolean isSuccess,
	Integer code,
	String message,
	SavingAccountResultDto result
) {
	public static GetSavingAccountResponse of(Boolean isSuccess, Integer code, String message,
		SavingAccountResultDto result) {
		return GetSavingAccountResponse.builder()
			.isSuccess(isSuccess)
			.code(code)
			.message(message)
			.result(result)
			.build();
	}
}
