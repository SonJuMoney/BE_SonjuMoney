package com.hana4.sonjumoney.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.response.CallRecommendationResponse;
import com.hana4.sonjumoney.service.CallService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Calls", description = "화상통하 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calls")
public class CallController {

	private final CallService callService;

	@GetMapping("/recommendations")
	public ResponseEntity<List<CallRecommendationResponse>> getRecommendations(Authentication authentication,
		@RequestParam(value = "target_id") Long targetId) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		List<CallRecommendationResponse> response = callService.getRecommendations(userId, targetId);
		return ResponseEntity.ok().body(response);
	}
}
