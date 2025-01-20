package com.hana4.sonjumoney.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.service.AlarmService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

	private final AlarmService alarmService;
}
