package com.hana4.sonjumoney.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.request.AccountRequest;
import com.hana4.sonjumoney.dto.response.AccountResponse;
import com.hana4.sonjumoney.service.AccountService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping("/accounts")
	public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request,
		Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);

		AccountResponse response;
		if (request.userId() == null) {
			// 본인 계좌 등록
			response = accountService.makeAccount(userId, request.mockaccId());
		} else {
			// 자녀 계좌 등록
			response = accountService.makeAccount(request.userId(), request.mockaccId());
		}

		return ResponseEntity.ok().body(response);
	}
}
