package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SwitchAccountRequest(
	@JsonProperty(value = "target_id", required = true)
	Long targetId
) {
}
