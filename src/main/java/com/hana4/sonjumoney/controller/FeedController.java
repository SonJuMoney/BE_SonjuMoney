package com.hana4.sonjumoney.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.request.CreateFeedRequest;
import com.hana4.sonjumoney.dto.response.CreateFeedResponse;
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
		@RequestPart(value = "images") MultipartFile[] images,
		@RequestPart(value = "data") CreateFeedRequest createFeedRequest
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(feedService.saveNormalFeed(AuthenticationUtil.getUserId(authentication), images, createFeedRequest));
	}
}
