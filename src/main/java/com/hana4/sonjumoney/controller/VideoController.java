package com.hana4.sonjumoney.controller;

import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.service.VideoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {
	private final VideoService videoService;

	@GetMapping("/stream")
	public ResponseEntity<ResourceRegion> streamingVideo(@RequestHeader HttpHeaders httpHeaders,
		@RequestParam("video") String pathStr) {
		return videoService.streamingVideo(httpHeaders, pathStr);
	}

}
