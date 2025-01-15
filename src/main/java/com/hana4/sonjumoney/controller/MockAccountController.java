package com.hana4.sonjumoney.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.response.MockAccountResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.security.model.CustomUserDetails;
import com.hana4.sonjumoney.service.MockAccountService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MockAccountController {
	
	private final MockAccountService mockAccountService;

	@GetMapping("/mock/accounts")
	public ResponseEntity<List<MockAccountResponse>> mockAccountList(@RequestParam(value = "user_id", required = false) Long childId, Authentication authentication){
		Long userId = AuthenticationUtil.getUserId(authentication);

		List<MockAccountResponse> response;
		if(childId == null){
			response = mockAccountService.findMyMockAccounts(userId);
		}else{
			response = mockAccountService.findChildMockAccounts(userId, childId);
		}

		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/mock/accounts/pin")
	public ResponseEntity<?> validateMockAccountPin(@RequestParam("pin") String pin, @RequestParam("mockacc_id") Long mockAccId){
		Boolean response = mockAccountService.checkMockAccountPin(pin, mockAccId);
		if(response){
			return ResponseEntity.ok().body("Mock계좌 비밀번호 일치");
		}
		return ResponseEntity.status(401).body("Mock계좌 비밀번호 불일치");
	}
}
