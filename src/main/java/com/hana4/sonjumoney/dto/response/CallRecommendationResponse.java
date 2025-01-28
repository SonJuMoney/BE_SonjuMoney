package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record CallRecommendationResponse(
	String topic
) {
	public static CallRecommendationResponse of(String topic) {
		return CallRecommendationResponse.builder()
			.topic(topic)
			.build();
	}
}
