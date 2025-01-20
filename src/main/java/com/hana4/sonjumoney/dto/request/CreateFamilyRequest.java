package com.hana4.sonjumoney.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.dto.InviteUserDto;

import lombok.Builder;

@Builder
public record CreateFamilyRequest(
	@JsonProperty("family_name")
	String familyName,
	String role,
	@JsonProperty(value = "add_members",required = false)
	List<InviteUserDto> addMembers,
	@JsonProperty(value = "add_children",required = false)
	List<Long> addChildren
) {
}
