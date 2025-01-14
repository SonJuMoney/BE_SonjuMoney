package com.hana4.sonjumoney.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtil jwtUtil;

	// @PostMapping("/reissue")
	// public ResponseEntity<ReissueResponse> reissue(@CookieValue(name = "refresh_token") String refreshToken) {
	// }
}
