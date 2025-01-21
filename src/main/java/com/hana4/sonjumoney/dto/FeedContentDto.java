package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.hana4.sonjumoney.domain.enums.FeedType;

import lombok.Builder;

@Builder
public record FeedContentDto(
	Long feed_id,
	Long writer_id,
	String writer_name,
	Boolean is_mine,
	String writer_image,
	FeedType feed_type,
	String message,
	Boolean is_update,
	LocalDateTime created_at,
	List<FeedContentContentDto> contents,
	List<FeedContentCommentDto> comments
) {
	public static FeedContentDto of(Long feed_id, Long writer_id, String writer_name, Boolean is_mine,
		String writer_image, FeedType feed_type, String message, Boolean is_update, LocalDateTime created_at,
		List<FeedContentContentDto> contents, List<FeedContentCommentDto> comments) {
		return FeedContentDto.builder()
			.feed_id(feed_id)
			.writer_id(writer_id)
			.writer_name(writer_name)
			.is_mine(is_mine)
			.writer_image(writer_image)
			.feed_type(feed_type)
			.message(message)
			.is_update(is_update)
			.created_at(created_at)
			.contents(contents)
			.comments(comments)
			.build();
	}
}
