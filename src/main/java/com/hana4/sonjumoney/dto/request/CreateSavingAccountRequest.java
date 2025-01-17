package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record CreateSavingAccountRequest(
	@JsonProperty(value = "account_type_id", required = true)
	Long accountTypeId,
	@JsonProperty(value = "message", required = true)
	String message,
	@JsonProperty(value = "holder_resident_num", required = true)
	String holderResidentNum,
	@JsonProperty(value = "account_password", required = true)
	String accountPassword,
	@JsonProperty(value = "auto_transferable", required = true)
	Boolean autoTransferable,
	@JsonProperty(value = "user_id", required = true)
	Long userId,
	@JsonProperty(value = "withdrawal_account_id", required = false)
	Long withdrawalAccountId,
	@JsonProperty(value = "deposit_account_id", required = false)
	Long depositAccountId,
	@JsonProperty(value = "pay_day", required = false)
	Integer payDay,
	@JsonProperty(value = "pay_amount", required = false)
	Long payAmount

) {
}
