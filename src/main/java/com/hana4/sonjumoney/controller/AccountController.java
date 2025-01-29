package com.hana4.sonjumoney.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.request.CreateAccountRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingAccountRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingsMessageRequest;
import com.hana4.sonjumoney.dto.request.SendMoneyRequest;
import com.hana4.sonjumoney.dto.response.AccountInfoResponse;
import com.hana4.sonjumoney.dto.response.CreateAccountResponse;
import com.hana4.sonjumoney.dto.response.CreateSavingAccountResponse;
import com.hana4.sonjumoney.dto.response.CreateSavingsMessageResponse;
import com.hana4.sonjumoney.dto.response.GetSavingAccountLimitResponse;
import com.hana4.sonjumoney.dto.response.GetSavingAccountResponse;
import com.hana4.sonjumoney.dto.response.GetTransactionHistoryResponse;
import com.hana4.sonjumoney.dto.response.SavingAccountResponse;
import com.hana4.sonjumoney.dto.response.TransferResponse;
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

	/*내 계좌 이체내역 조회*/
	@GetMapping("/{account_id}/transactions")
	public ResponseEntity<GetTransactionHistoryResponse> getTransactionHistory(
		@PathVariable("account_id") Long accountId, @RequestParam Integer page,
		Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(accountService.getTransactions(userId, accountId, page));
	}

	@PostMapping("/savings")
	public ResponseEntity<CreateSavingAccountResponse> createSavingAccount(
		@RequestBody CreateSavingAccountRequest request, Authentication authentication) {
		return ResponseEntity.ok()
			.body(accountService.makeSavingAccount(request, AuthenticationUtil.getUserId(authentication)));
	}

	@GetMapping("/savings")
	public ResponseEntity<SavingAccountResponse> getSavingAccount(Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(accountService.findSavingAccounts(userId));
	}

	@PostMapping("/savings/{account_id}/transfer")
	public ResponseEntity<TransferResponse> sendMoneyToSavingAccount(@PathVariable("account_id") Long accountId,
		@RequestBody
		SendMoneyRequest request, Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(accountService.sendMoneyProcess(accountId, request, userId));
	}

	/*특정 적금 납입내역 조회*/
	@GetMapping("/savings/{account_id}")
	public ResponseEntity<GetSavingAccountResponse> getSavingAccountTransaction(@RequestParam Integer page,
		Authentication authentication,
		@PathVariable(value = "account_id") Long accountId) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(accountService.getSavingAccount(userId, accountId, page));
	}

	/* 적금 납입 한도 조회*/
	@GetMapping("/savings/{account_id}/limit")
	public ResponseEntity<GetSavingAccountLimitResponse> getLimit(Authentication authentication,
		@PathVariable(value = "account_id") Long accountId) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(accountService.getSavingAccountLimit(userId, accountId));
	}

	@PostMapping("/savings/{auto_transfer_id}/message")
	public ResponseEntity<CreateSavingsMessageResponse> createSavingsMessage(Authentication authentication,
		@PathVariable(value = "auto_transfer_id") Long autoTransferId,
		@RequestBody CreateSavingsMessageRequest request) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(accountService.createSavingsMessage(userId, autoTransferId, request));
	}
}
