package com.hana4.sonjumoney.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SavingAccountResponse(
	@JsonProperty("is_child")
	boolean isChild,
	@JsonProperty("savings")
	List<SavingAccountInfoResponse> savings
) {
	public static SavingAccountResponse of(boolean isChild, List<SavingAccountInfoResponse> savings) {
		return SavingAccountResponse.builder()
			.isChild(isChild)
			.savings(savings)
			.build();
	}
}
