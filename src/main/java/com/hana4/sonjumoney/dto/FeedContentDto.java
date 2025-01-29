package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.hana4.sonjumoney.domain.Feed;
import com.hana4.sonjumoney.domain.enums.FeedType;
import com.hana4.sonjumoney.domain.enums.MemberRole;

import lombok.Builder;

@Builder
public record FeedContentDto(
	Long feed_id,
	Long writer_id,
	String writer_name,
	MemberRole member_role,
	Boolean is_mine,
	String writer_image,
	FeedType feed_type,
	String message,
	Integer like,
	Boolean is_update,
	LocalDateTime created_at,
	List<FeedContentContentDto> contents,
	List<FeedContentCommentDto> comments
) {
	public static FeedContentDto from(Feed feed, Long userId, List<FeedContentContentDto> contents,
		List<FeedContentCommentDto> comments) {
		return FeedContentDto.builder()
			.feed_id(feed.getId())
			.writer_id(feed.getMember().getId())
			.member_role(feed.getMember().getMemberRole())
			.is_mine(feed.getMember().getUser().getId().equals(userId))
			.writer_image(feed.getMember().getUser().getProfileLink())
			.feed_type(feed.getFeedType())
			.message(feed.getFeedMessage())
			.like(feed.getLikes())
			.is_update(!feed.getCreatedAt().equals(feed.getUpdatedAt()))
			.created_at(feed.getCreatedAt())
			.contents(contents)
			.comments(comments)
			.build();
	}
	public static FeedContentDto of(Long feed_id, Long writer_id, String writer_name, MemberRole member_role,
		Boolean is_mine,
		String writer_image, FeedType feed_type, String message, Integer like, Boolean is_update,
		LocalDateTime created_at,
		List<FeedContentContentDto> contents, List<FeedContentCommentDto> comments) {
		return FeedContentDto.builder()
			.feed_id(feed_id)
			.writer_id(writer_id)
			.writer_name(writer_name)
			.member_role(member_role)
			.is_mine(is_mine)
			.writer_image(writer_image)
			.feed_type(feed_type)
			.message(message)
			.like(like)
			.is_update(is_update)
			.created_at(created_at)
			.contents(contents)
			.comments(comments)
			.build();
	}
}
