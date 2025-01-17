package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record CreateFamilyResponse(
	Integer code,
	Long familyId
) {
	public static CreateFamilyResponse of(Integer code,Long familyId) {
		return CreateFamilyResponse.builder()
			.code(code)
			.familyId(familyId)
			.build();
	}
}
