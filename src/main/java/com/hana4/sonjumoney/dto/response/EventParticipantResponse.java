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

	@JsonProperty("user_name")
	String userName
) {
	public static EventParticipantResponse of(EventParticipant participant) {
		return EventParticipantResponse.builder()
			.participationId(participant.getId())
			.memberId(participant.getMember().getId())
			.userName(participant.getMember().getUser().getUsername())
			.build();
	}
}

