package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record CommentInfoDto(
	@JsonProperty("comment_id")
	Long commentId,
	@JsonProperty("writer_id")
	Long writerId,
	@JsonProperty("writer_name")
	String writerName,
	@JsonProperty("writer_image")
	String writerImage,
	String message,
	@JsonProperty("is_update")
	Boolean isUpdate,
	@JsonProperty("created_at")
	LocalDateTime createdAt
	) {
	public static CommentInfoDto of(
		Long commentId,
		Long writerId,
		String writerName,
		String writerImage,
		String message,
		LocalDateTime createdAt
	) {
		return CommentInfoDto.builder()
			.commentId(commentId)
			.writerId(writerId)
			.writerName(writerName)
			.writerImage(writerImage)
			.message(message)
			.createdAt(createdAt)
			.build();
	}
}
