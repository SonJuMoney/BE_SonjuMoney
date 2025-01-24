package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record CreateSavingAccountRequest(

	@JsonProperty(value = "user_id", required = true)
	Long userId,
	@JsonProperty(value = "message", required = true)
	String message,
	@JsonProperty(value = "account_password", required = true)
	String accountPassword,
	@JsonProperty(value = "auto_transferable", required = true)
	Boolean autoTransferable,
	@JsonProperty(value = "pay_day", required = false)
	Integer payDay,
	@JsonProperty(value = "pay_amount", required = false)
	Long payAmount

) {
}
