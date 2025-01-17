package com.hana4.sonjumoney.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.CreateFamilyResponse;
import com.hana4.sonjumoney.service.FamilyService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families")
public class FamilyController {
	private final FamilyService familyService;

	@PostMapping
	public ResponseEntity<CreateFamilyResponse> createFamily(Authentication authentication,
		@RequestBody CreateFamilyRequest createFamilyRequest) {
		Long familyId= familyService.createFamily(AuthenticationUtil.getUserId(authentication),
			createFamilyRequest);
		return ResponseEntity.created(
			URI.create("/families/" + familyId)).body(CreateFamilyResponse.of(201, familyId));
	}

}
