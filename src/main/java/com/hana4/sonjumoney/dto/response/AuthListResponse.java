package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.Gender;
import com.hana4.sonjumoney.domain.enums.Role;

import lombok.Builder;

@Builder
public record AuthListResponse(
	@JsonProperty(value = "user_id")
	Long userId,
	Role role,
	String name,
	String profile,
	Gender gender
) {
	public static AuthListResponse of(Long userId, Role role, String name, String profile, Gender gender) {
		return AuthListResponse.builder()
			.userId(userId)
			.role(role)
			.name(name)
			.profile(profile)
			.gender(gender)
			.build();
	}
}
