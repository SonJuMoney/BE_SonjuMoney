package com.hana4.sonjumoney.dto.request;

import lombok.Builder;

@Builder
public record SendMoneyRequest(
	Long amount,
	String message,
	boolean status
) {
}
