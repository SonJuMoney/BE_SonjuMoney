package com.hana4.sonjumoney.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.request.CreateFeedRequest;
import com.hana4.sonjumoney.dto.response.CreateFeedResponse;
import com.hana4.sonjumoney.dto.response.FeedResponse;
import com.hana4.sonjumoney.service.FeedService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedController {
	private final FeedService feedService;

	@PostMapping
	public ResponseEntity<CreateFeedResponse> createFeed(
		Authentication authentication,
		@RequestPart(value = "images", required = false) MultipartFile[] images,
		@RequestPart(value = "data") CreateFeedRequest createFeedRequest
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(feedService.saveNormalFeed(AuthenticationUtil.getUserId(authentication), images, createFeedRequest));
	}

	@DeleteMapping("/{feed_id}")
	public ResponseEntity<Void> deleteFeed(Authentication authentication,
		@PathVariable(value = "feed_id") Long feedId) {
		feedService.deleteFeedById(AuthenticationUtil.getUserId(authentication), feedId);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	public ResponseEntity<FeedResponse> getFeed(Authentication authentication,
		@RequestParam(value = "family_id") Long familyId, @RequestParam(value = "page") Integer page) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		FeedResponse response = feedService.getFeeds(userId, familyId, page);
		return ResponseEntity.ok().body(response);
	}
}
