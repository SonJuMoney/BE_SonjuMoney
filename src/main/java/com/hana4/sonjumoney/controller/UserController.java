package com.hana4.sonjumoney.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.response.GetChildrenResponse;
import com.hana4.sonjumoney.dto.response.UpdateProfileResponse;
import com.hana4.sonjumoney.dto.response.UserInfoResponse;
import com.hana4.sonjumoney.service.UserService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
	private final UserService userService;

	@GetMapping
	public ResponseEntity<UserInfoResponse> getUser(Authentication authentication) {
		System.out.println("userinfo 컨르롤러 진입");
		return ResponseEntity.ok().body(userService.getUserByUserId(AuthenticationUtil.getUserId(authentication)));
	}

	@GetMapping("/children")
	public ResponseEntity<List<GetChildrenResponse>> getChildren(Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(userService.getChildren(userId));
	}

	@PatchMapping("/profiles")
	public ResponseEntity<UpdateProfileResponse> updateProfile(Authentication authentication,
		@RequestPart(value = "file") MultipartFile file) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		UpdateProfileResponse response = userService.updateProfile(userId, file);
		return ResponseEntity.ok().body(response);
	}
}
