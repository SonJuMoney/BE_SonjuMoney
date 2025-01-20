package com.hana4.sonjumoney.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.response.AlarmResponse;
import com.hana4.sonjumoney.service.AlarmService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

	private final AlarmService alarmService;

	@GetMapping
	public ResponseEntity<AlarmResponse> getAlarms(@RequestParam Integer page, Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		AlarmResponse response = alarmService.getAlarms(userId, page);
		return ResponseEntity.ok().body(response);
	}
}
