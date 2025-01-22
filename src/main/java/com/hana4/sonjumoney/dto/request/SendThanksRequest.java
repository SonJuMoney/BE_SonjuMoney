package com.hana4.sonjumoney.dto.request;

import lombok.Builder;

@Builder
public record SendThanksRequest(
	String message
) {
}
