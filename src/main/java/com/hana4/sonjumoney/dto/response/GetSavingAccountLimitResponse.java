package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record GetSavingAccountLimitResponse(
	@JsonProperty("receiver_name")
	String receiverName,

	@JsonProperty("total_payment")
	Long totalPayment,

	@JsonProperty("month_payment")
	Integer monthPayment,

	@JsonProperty("month_available_amount")
	Integer monthAvailableAmount

) {
	public static GetSavingAccountLimitResponse of(String receiverName, Long totalPayment, Integer monthPayment,
		Integer monthAvailableAmount) {
		return GetSavingAccountLimitResponse.builder()
			.receiverName(receiverName)
			.totalPayment(totalPayment)
			.monthPayment(monthPayment)
			.monthAvailableAmount(monthAvailableAmount)
			.build();
	}
}
