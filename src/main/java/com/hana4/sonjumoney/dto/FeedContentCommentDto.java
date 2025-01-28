package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.hana4.sonjumoney.domain.Comment;
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

	public static FeedContentCommentDto from(Comment comment, Long userId) {
		return FeedContentCommentDto.builder()
			.comment_id(comment.getId())
			.writer_id(comment.getMember().getUser().getId())
			.writer_name(comment.getMember().getUser().getUsername())
			.member_role(comment.getMember().getMemberRole())
			.is_mine(comment.getMember().getUser().getId().equals(userId))
			.writer_image(comment.getMember().getUser().getProfileLink())
			.message(comment.getMessage())
			.is_update(!comment.getCreatedAt().equals(comment.getUpdatedAt()))
			.created_at(comment.getCreatedAt())
			.build();
	}
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
