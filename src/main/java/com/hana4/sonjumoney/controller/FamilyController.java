package com.hana4.sonjumoney.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.response.GetFamilyResponse;
import com.hana4.sonjumoney.service.FamilyService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

	private FamilyService familyService;

	@GetMapping
	public ResponseEntity<List<GetFamilyResponse>> getFamilies(Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		List<GetFamilyResponse> response = familyService.findFamilies(userId);
		return ResponseEntity.ok().body(response);
	}
}