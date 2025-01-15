package com.hana4.sonjumoney.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.request.EventAddRequest;
import com.hana4.sonjumoney.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/events")
public class EventController {
	private final EventService eventService;

	@PostMapping()
	public ResponseEntity<?> addEvent(@RequestParam Long familyId, @RequestBody EventAddRequest eventAddRequest) {
		eventService.addEvent(familyId, eventAddRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
