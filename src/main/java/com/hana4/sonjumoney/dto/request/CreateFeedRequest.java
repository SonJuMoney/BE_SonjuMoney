package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateFeedRequest(
	@JsonProperty(value = "family_id")
	Long familyId,
	String message
) {
}
