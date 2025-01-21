package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record AcceptInvitationResponse(
	Integer code,
	@JsonProperty(value = "family_id")
	Long familyId
) {
	public static AcceptInvitationResponse of(Integer code, Long familyId) {
		return AcceptInvitationResponse.builder()
			.code(code)
			.familyId(familyId)
			.build();
	}
}
