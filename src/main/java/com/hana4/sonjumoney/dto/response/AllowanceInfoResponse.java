package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.Allowance;

import lombok.Builder;

@Builder
public record AllowanceInfoResponse(
	@JsonProperty(value = "allowance_id")
	Long allowanceId,
	@JsonProperty(value = "sender_user_id")
	Long senderUserId,
	@JsonProperty(value = "sender_name")
	String senderName,
	@JsonProperty(value = "sender_profile")
	String senderProfile,
	@JsonProperty(value = "amount")
	Long amount
) {
	public static AllowanceInfoResponse from(Allowance allowance) {
		return AllowanceInfoResponse.builder()
			.allowanceId(allowance.getId())
			.senderUserId(allowance.getSender().getUser().getId())
			.senderName(allowance.getSender().getUser().getUsername())
			.senderProfile(allowance.getSender().getUser().getProfileLink())
			.amount(allowance.getAmount())
			.build();
	}
}
