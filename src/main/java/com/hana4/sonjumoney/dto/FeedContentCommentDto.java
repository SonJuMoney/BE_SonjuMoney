package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.hana4.sonjumoney.domain.enums.MemberRole;

import lombok.Builder;

@Builder
public record FeedContentCommentDto(
	Long comment_id,
	Long writer_id,
	String writer_name,
	MemberRole member_role,
	Boolean is_mine,
	String writer_image,
	String message,
	Boolean is_update,
	LocalDateTime created_at
) {
	public static FeedContentCommentDto of(Long comment_id, Long writer_id, String writer_name, MemberRole member_role,
		Boolean is_mine,
		String writer_image,
		String message,
		Boolean is_update, LocalDateTime created_at) {
		return FeedContentCommentDto.builder()
			.comment_id(comment_id)
			.writer_id(writer_id)
			.writer_name(writer_name)
			.member_role(member_role)
			.is_mine(is_mine)
			.writer_image(writer_image)
			.message(message)
			.is_update(is_update)
			.created_at(created_at)
			.build();
	}
}
