package com.hana4.sonjumoney.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.JwtTokenDto;
import com.hana4.sonjumoney.dto.request.AuthPinRequest;
import com.hana4.sonjumoney.dto.request.SignUpChildRequest;
import com.hana4.sonjumoney.dto.request.SignUpRequest;
import com.hana4.sonjumoney.dto.request.SwitchAccountRequest;
import com.hana4.sonjumoney.dto.response.AuthListResponse;
import com.hana4.sonjumoney.dto.response.DuplicationResponse;
import com.hana4.sonjumoney.dto.response.PinValidResponse;
import com.hana4.sonjumoney.dto.response.ReissueResponse;
import com.hana4.sonjumoney.dto.response.SignUpChildResponse;
import com.hana4.sonjumoney.dto.response.SignUpResponse;
import com.hana4.sonjumoney.service.AuthService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/reissue")
	public ResponseEntity<ReissueResponse> reissue(@CookieValue(name = "refresh_token") String refreshToken) {
		ReissueResponse reissueResponse = authService.reissue(refreshToken);
		return ResponseEntity.ok().body(reissueResponse);
	}

	@GetMapping("/id-duplication")
	public ResponseEntity<DuplicationResponse> getIdDuplication(@RequestParam(name = "id") String id) {
		DuplicationResponse duplicationResponse = authService.getIdDuplication(id);
		return ResponseEntity.ok().body(duplicationResponse);
	}

	@GetMapping("/resident-duplication")
	public ResponseEntity<DuplicationResponse> getResidentDulplication(
		@RequestParam(name = "resident") String residentNum) {
		DuplicationResponse duplicationResponse = authService.getResidentDuplication(residentNum);
		return ResponseEntity.ok().body(duplicationResponse);
	}

	@GetMapping("/phone-duplication")
	public ResponseEntity<DuplicationResponse> getPhoneDuplication(@RequestParam(name = "phone") String phoneNum) {
		DuplicationResponse duplicationResponse = authService.getPhoneDuplication(phoneNum);
		return ResponseEntity.ok().body(duplicationResponse);
	}

	@PostMapping("/sign-up")
	public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
		SignUpResponse signUpResponse = authService.signUp(signUpRequest);
		return ResponseEntity.ok().body(signUpResponse);
	}

	@PostMapping("/sign-up-child")
	public ResponseEntity<SignUpChildResponse> signUpChild(@RequestBody SignUpChildRequest signUpChildRequest,
		Authentication authentication) {
		Long parentId = AuthenticationUtil.getUserId(authentication);
		SignUpChildResponse response = authService.signUpChild(signUpChildRequest, parentId);
		return ResponseEntity.ok().body(response);

	}

	@PostMapping("/pin")
	public ResponseEntity<PinValidResponse> validatePin(@RequestBody AuthPinRequest pinRequest,
		Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		PinValidResponse pinValidResponse = authService.validatePin(pinRequest, userId);
		return ResponseEntity.ok().body(pinValidResponse);
	}

	@GetMapping("/list")
	public ResponseEntity<List<AuthListResponse>> getAuthList(Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		List<AuthListResponse> response = authService.getAuthList(userId);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/switch-account")
	public ResponseEntity<JwtTokenDto> switchAccount(Authentication authentication,
		@RequestBody SwitchAccountRequest switchAccountRequest) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		JwtTokenDto response = authService.switchAccount(userId, switchAccountRequest);
		return ResponseEntity.ok().body(response);
	}
}
