package com.hana4.sonjumoney.dto.response;

import com.hana4.sonjumoney.dto.TransactionHistoryResultDto;

import lombok.Builder;

@Builder
public record GetTransactionHistoryResponse(Boolean isSuccess,
											Integer code,
											String message,
											TransactionHistoryResultDto result
) {
	public static GetTransactionHistoryResponse of(Boolean isSuccess, Integer code, String message,
		TransactionHistoryResultDto result) {
		return GetTransactionHistoryResponse.builder()
			.isSuccess(isSuccess)
			.code(code)
			.message(message)
			.result(result)
			.build();
	}
}
