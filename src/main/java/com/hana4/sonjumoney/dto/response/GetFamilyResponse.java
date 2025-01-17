package com.hana4.sonjumoney.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record GetFamilyResponse(
	@JsonProperty("family_id")
	Long familyId,
	@JsonProperty("family_name")
	String familyName,
	@JsonProperty("members")
	List<MemberResponse> members
) {
	public static GetFamilyResponse of(
		Long familyId,
		String familyName,
		List<MemberResponse> members
	) {
		return GetFamilyResponse.builder()
			.familyId(familyId)
			.familyName(familyName)
			.members(members)
			.build();
	}
}
