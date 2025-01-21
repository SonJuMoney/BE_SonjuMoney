package com.hana4.sonjumoney.dto.response;

import java.util.List;

import com.hana4.sonjumoney.domain.Member;

import lombok.Builder;

@Builder
public record GetFamilyMemberResponse(
	Long familyId,
	String familyName,
	List<MemberResponse> members
) {
	public static GetFamilyMemberResponse of(Long familyId, String familyName, List<Member> members) {
		return GetFamilyMemberResponse.builder()
			.familyId(familyId)
			.familyName(familyName)
			.members(members.stream()
				.map(member -> MemberResponse.of(member.getId(), member.getUser().getId(),
					member.getUser().getUsername(), member.getMemberRole().getValue())).toList())
			.build();
	}
}
