package com.hana4.sonjumoney.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.response.DuplicationResponse;
import com.hana4.sonjumoney.dto.response.ReissueResponse;
import com.hana4.sonjumoney.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/reissue")
	public ResponseEntity<ReissueResponse> reissue(@CookieValue(name = "refresh_token") String refreshToken) {
		ReissueResponse reissueResponse = authService.reissue(refreshToken);
		return ResponseEntity.ok().body(reissueResponse);
	}

	@GetMapping("/id-duplication")
	public ResponseEntity<DuplicationResponse> getDuplication(@RequestParam(name = "id") String id) {
		DuplicationResponse duplicationResponse = authService.getDuplication(id);
		return ResponseEntity.ok(duplicationResponse);
	}
}
