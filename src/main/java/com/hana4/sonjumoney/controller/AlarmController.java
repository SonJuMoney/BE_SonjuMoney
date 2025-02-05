package com.hana4.sonjumoney.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.dto.response.AlarmResponse;
import com.hana4.sonjumoney.dto.response.AlarmStatusResponse;
import com.hana4.sonjumoney.dto.response.UpdateAlarmResponse;
import com.hana4.sonjumoney.service.AlarmService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Alarms", description = "알림 관련 API")
@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {

	private final AlarmService alarmService;

	@GetMapping
	public ResponseEntity<AlarmResponse> getAlarms(@RequestParam Integer page, Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		AlarmResponse response = alarmService.getAlarms(userId, page);
		return ResponseEntity.ok().body(response);
	}

	@PatchMapping("/{alarm_id}")
	public ResponseEntity<UpdateAlarmResponse> updateAlarm(@PathVariable("alarm_id") Long alarm_id) {
		UpdateAlarmResponse response = alarmService.updateAlarm(alarm_id);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/status/{alarm_status}")
	public ResponseEntity<AlarmStatusResponse> getAlarmStatus(@PathVariable("alarm_status") AlarmStatus alarmStatus,
		Authentication authentication) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		AlarmStatusResponse response = alarmService.getAlarmStatus(userId, alarmStatus);
		return ResponseEntity.ok().body(response);
	}
}
