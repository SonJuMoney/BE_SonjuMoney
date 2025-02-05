package com.hana4.sonjumoney.controller;

import java.util.List;

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

import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.CreateFamilyResponse;
import com.hana4.sonjumoney.dto.response.GetFamilyMemberResponse;
import com.hana4.sonjumoney.dto.response.GetFamilyResponse;
import com.hana4.sonjumoney.service.FamilyService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Families", description = "가족 관련 API")
@RestController
@RequestMapping("/api/v1/families")
@RequiredArgsConstructor
public class FamilyController {

	private final FamilyService familyService;

	@GetMapping
	public ResponseEntity<List<GetFamilyResponse>> getFamilies(Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		List<GetFamilyResponse> response = familyService.findFamilies(userId);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping
	public ResponseEntity<CreateFamilyResponse> createFamily(Authentication authentication,
		@RequestBody CreateFamilyRequest createFamilyRequest) {
		Long familyId = familyService.createFamily(AuthenticationUtil.getUserId(authentication),
			createFamilyRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(CreateFamilyResponse.of(201, familyId));
	}

	@GetMapping("/{family_id}/members")
	public ResponseEntity<GetFamilyMemberResponse> getFamilyMember(@PathVariable("family_id") Long familyId,
		@RequestParam String range, Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(familyService.findFamilyMembers(userId, familyId, range));
	}
}
