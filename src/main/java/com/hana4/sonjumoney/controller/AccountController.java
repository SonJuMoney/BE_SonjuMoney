package com.hana4.sonjumoney.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.request.CreateAccountRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingAccountRequest;
import com.hana4.sonjumoney.dto.response.AccountInfoResponse;
import com.hana4.sonjumoney.dto.response.CreateAccountResponse;
import com.hana4.sonjumoney.dto.response.CreateSavingAccountResponse;
import com.hana4.sonjumoney.dto.response.SavingAccountInfoResponse;
import com.hana4.sonjumoney.service.AccountService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping
	public ResponseEntity<CreateAccountResponse> createAccount(@RequestBody CreateAccountRequest request,
		Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);

		CreateAccountResponse response;
		if (request.userId() == null) {
			// 본인 계좌 등록
			response = accountService.makeAccount(userId, request.mockaccId());
		} else {
			// 자녀 계좌 등록
			response = accountService.makeAccount(request.userId(), request.mockaccId());
		}

		return ResponseEntity.ok().body(response);
	}

	@GetMapping
	public ResponseEntity<AccountInfoResponse> getAccount(Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(accountService.getAccountByUserId(userId));
	}

	@PostMapping("/savings")
	public ResponseEntity<CreateSavingAccountResponse> createSavingAccount(
		@RequestBody CreateSavingAccountRequest request, Authentication authentication) {
		return ResponseEntity.ok()
			.body(accountService.makeSavingAccount(request, AuthenticationUtil.getUserId(authentication)));
	}

	@GetMapping("/savings")
	public ResponseEntity<List<SavingAccountInfoResponse>> getSavingAccount(Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(accountService.findSavingAccounts(userId));
	}
}
