package com.hana4.sonjumoney.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.request.PinValidRequest;
import com.hana4.sonjumoney.dto.response.MockAccountResponse;
import com.hana4.sonjumoney.dto.response.PinValidResponse;
import com.hana4.sonjumoney.service.MockAccountService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Mock Accounts", description = "목 계좌 관련 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MockAccountController {

	private final MockAccountService mockAccountService;

	@GetMapping("/mock/accounts")
	public ResponseEntity<List<MockAccountResponse>> mockAccountList(
		@RequestParam(value = "user_id", required = false) Long childId, Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);

		List<MockAccountResponse> response;
		if (childId == null) {
			response = mockAccountService.findMyMockAccounts(userId);
		} else {
			response = mockAccountService.findChildMockAccounts(userId, childId);
		}

		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/mock/accounts/pin")
	public ResponseEntity<PinValidResponse> validateMockAccountPin(@RequestBody PinValidRequest request) {
		PinValidResponse response = mockAccountService.checkMockAccountPin(request);
		return ResponseEntity.ok().body(response);
	}
}
