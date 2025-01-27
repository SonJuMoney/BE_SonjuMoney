package com.hana4.sonjumoney.controller;

import com.hana4.sonjumoney.dto.request.SendThanksRequest;
import com.hana4.sonjumoney.dto.response.AllowanceInfoResponse;
import com.hana4.sonjumoney.dto.response.SendAllowanceResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.request.SendAllowanceRequest;
import com.hana4.sonjumoney.dto.response.SendThanksResponse;
import com.hana4.sonjumoney.service.AllowanceService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Allowances",description = "용돈 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/allowances")
public class AllowanceController {
	private final AllowanceService allowanceService;

	@PostMapping
	public ResponseEntity<SendAllowanceResponse> sendAllowance(
			Authentication authentication,
			@RequestPart(value = "file", required = false) MultipartFile image,
			@RequestPart(value = "data") SendAllowanceRequest sendAllowanceRequest
	) {
		return ResponseEntity.ok()
				.body(allowanceService.sendAllowance(image, AuthenticationUtil.getUserId(authentication),
						sendAllowanceRequest));
	}

	@GetMapping("/{allowance_id}")
	public ResponseEntity<AllowanceInfoResponse> getAllowance(@PathVariable(value = "allowance_id") Long allowanceId) {
		return ResponseEntity.ok().body(allowanceService.getAllowanceById(allowanceId));
	}

	@PostMapping("/{allowance_id}/thanks")
	public ResponseEntity<SendThanksResponse> sendThanks(Authentication authentication,
		@PathVariable(value = "allowance_id") Long allowanceId,
		@RequestPart(value = "file", required = false) MultipartFile image,
		@RequestPart(value = "data") SendThanksRequest sendThanksRequest
	) {
		return ResponseEntity.ok()
			.body(allowanceService.sendThanks(image, AuthenticationUtil.getUserId(authentication), allowanceId,
				sendThanksRequest));
	}
}
