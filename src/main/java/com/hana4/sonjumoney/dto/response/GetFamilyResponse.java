package com.hana4.sonjumoney.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record GetFamilyResponse(
	@JsonProperty("family_id")
	Long familyId,
	@JsonProperty("family_name")
	String familyName,
	@JsonProperty("created_at")
	LocalDateTime createdAt,
	@JsonProperty("updated_at")
	LocalDateTime updatedAt
) {
	public static GetFamilyResponse of(
		Long familyId,
		String familyName,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		return GetFamilyResponse.builder()
			.familyId(familyId)
			.familyName(familyName)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
