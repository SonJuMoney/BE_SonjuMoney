package com.hana4.sonjumoney.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.request.EventAddRequest;
import com.hana4.sonjumoney.dto.response.EventResponse;
import com.hana4.sonjumoney.service.EventService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Events", description = "일정 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
	private final EventService eventService;

	@PostMapping()
	public ResponseEntity<EventResponse> addEvent(@RequestParam Long familyId,
		@RequestBody EventAddRequest eventAddRequest) {
		EventResponse eventResponse = eventService.addEvent(familyId, eventAddRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
	}

	@GetMapping()
	public ResponseEntity<List<EventResponse>> getAllEvents(@RequestParam Long familyId,
		@RequestParam(required = false) Integer year,
		@RequestParam(required = false) Integer month) {
		int getYear = (year == null) ? LocalDate.now().getYear() : year;
		int getMonth = (month == null) ? LocalDate.now().getMonthValue() : month;
		List<EventResponse> eventResponseList = eventService.getAllEvents(familyId, getYear, getMonth);
		return ResponseEntity.ok().body(eventResponseList);
	}
}
