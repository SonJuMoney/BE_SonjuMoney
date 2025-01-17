package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.MemberRole;

import lombok.Builder;

@Builder
public record MemberResponse(
	@JsonProperty("member_id")
	Long memberId,
	@JsonProperty("user_id")
	Long userId,
	@JsonProperty("member_name")
	String memberName,
	@JsonProperty("member_role")
	MemberRole memberRole
) {
	public static MemberResponse of(
		Long memberId,
		Long userId,
		String memberName,
		MemberRole memberRole
	) {
		return MemberResponse.builder()
			.memberId(memberId)
			.userId(userId)
			.memberName(memberName)
			.memberRole(memberRole)
			.build();
	}
}
