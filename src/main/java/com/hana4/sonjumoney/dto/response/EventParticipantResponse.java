package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.EventParticipant;

import lombok.Builder;

@Builder
public record EventParticipantResponse(
	@JsonProperty("participation_id")
	Long participationId,

	@JsonProperty("member_id")
	Long memberId,

	@JsonProperty("member_role")
	String memberRole,

	@JsonProperty("user_name")
	String userName,

	@JsonProperty("profile_link")
	String profileLink

) {
	public static EventParticipantResponse from(EventParticipant participant) {
		return EventParticipantResponse.builder()
			.participationId(participant.getId())
			.memberId(participant.getMember().getId())
			.memberRole(participant.getMember().getMemberRole().getValue())
			.userName(participant.getMember().getUser().getUsername())
			.profileLink(participant.getMember().getUser().getProfileLink())
			.build();
	}
}

