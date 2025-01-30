package com.hana4.sonjumoney.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.response.AcceptInvitationResponse;
import com.hana4.sonjumoney.service.InvitationService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Invitation", description = "가족 초대 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invitation")
public class InvitationController {
	private final InvitationService invitationService;

	@PostMapping("/{invitation_id}")
	public ResponseEntity<AcceptInvitationResponse> acceptInvitation(
		Authentication authentication,
		@PathVariable(value = "invitation_id") Long invitationId) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(invitationService.acceptInvitation(userId, invitationId));
	}
}
