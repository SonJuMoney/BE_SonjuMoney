package com.hana4.sonjumoney.controller;

import com.hana4.sonjumoney.dto.response.SendAllowanceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.request.SendAllowanceRequest;
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
			@RequestPart(value = "image", required = false) MultipartFile image,
			@RequestPart(value = "data") SendAllowanceRequest sendAllowanceRequest
	) {
		return ResponseEntity.ok()
				.body(allowanceService.sendAllowance(image, AuthenticationUtil.getUserId(authentication),
						sendAllowanceRequest));
	}
}
