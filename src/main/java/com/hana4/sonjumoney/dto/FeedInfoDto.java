package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.FeedType;

public record FeedInfoDto(
	@JsonProperty("feed_id")
	Long feedId,
	@JsonProperty("writer_id")
	Long writerId,
	@JsonProperty("writer_name")
	String writerName,
	@JsonProperty("is_mine")
	Boolean isMine,
	@JsonProperty("writer_image")
	String writerImage,
	@JsonProperty("feed_type")
	FeedType feedType,
	String message,
	@JsonProperty("is_update")
	Boolean isUpdate,
	@JsonProperty("created_at")
	LocalDateTime createdAt,
	@JsonProperty("contents")
	List<String>feedContent
) {
}
