package com.hana4.sonjumoney.dto.request;

public record SignInRequest(
	String authId,
	String password
) {
}
