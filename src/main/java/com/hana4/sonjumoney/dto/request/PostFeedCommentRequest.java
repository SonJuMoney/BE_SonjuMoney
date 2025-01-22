package com.hana4.sonjumoney.dto.request;

import lombok.Builder;

@Builder
public record PostFeedCommentRequest(
	String message
) {
}
