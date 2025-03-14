package com.hana4.sonjumoney.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.request.CreateFeedRequest;
import com.hana4.sonjumoney.dto.request.PostFeedCommentRequest;
import com.hana4.sonjumoney.dto.response.CreateFeedResponse;
import com.hana4.sonjumoney.dto.response.DeleteFeedCommentResponse;
import com.hana4.sonjumoney.dto.response.DeleteFeedResponse;
import com.hana4.sonjumoney.dto.response.FeedLikeResponse;
import com.hana4.sonjumoney.dto.response.FeedResponse;
import com.hana4.sonjumoney.dto.response.PostFeedCommentResponse;
import com.hana4.sonjumoney.service.FeedService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Feeds", description = "피드 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {
	private final FeedService feedService;

	@PostMapping
	public ResponseEntity<CreateFeedResponse> createFeed(
		Authentication authentication,
		@RequestPart(value = "files", required = false) MultipartFile[] images,
		@RequestPart(value = "data") CreateFeedRequest createFeedRequest
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(feedService.saveFamilyFeed(AuthenticationUtil.getUserId(authentication), images, createFeedRequest));
	}

	@DeleteMapping("/{feed_id}")
	public ResponseEntity<DeleteFeedResponse> deleteFeed(Authentication authentication,
		@PathVariable(value = "feed_id") Long feedId) {
		return ResponseEntity.ok().body(
			feedService.deleteFeedById(AuthenticationUtil.getUserId(authentication), feedId));
	}

	@GetMapping
	public ResponseEntity<FeedResponse> getFeed(Authentication authentication,
		@RequestParam(value = "family_id") Long familyId, @RequestParam(value = "page") Integer page) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		FeedResponse response = feedService.getFeeds(userId, familyId, page);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/{feed_id}/likes")
	public ResponseEntity<FeedLikeResponse> postFeedLike(Authentication authentication,
		@PathVariable(value = "feed_id") Long feedId) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		FeedLikeResponse response = feedService.postFeedLike(userId, feedId);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/{feed_id}/comments")
	public ResponseEntity<PostFeedCommentResponse> postFeedComment(Authentication authentication,
		@PathVariable(value = "feed_id") Long feedId, @RequestBody
	PostFeedCommentRequest postFeedCommentRequest) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		PostFeedCommentResponse response = feedService.postFeedComment(userId, feedId, postFeedCommentRequest);
		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping("/comments/{comment_id}")
	public ResponseEntity<DeleteFeedCommentResponse> deleteFeedComment(Authentication authentication,
		@PathVariable(value = "comment_id") Long commentId) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		DeleteFeedCommentResponse response = feedService.deleteFeedComment(userId, commentId);
		return ResponseEntity.ok().body(response);
	}
}
