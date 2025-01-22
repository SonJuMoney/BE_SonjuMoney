package com.hana4.sonjumoney.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.request.AddEventRequest;
import com.hana4.sonjumoney.dto.request.UpdateEventRequest;
import com.hana4.sonjumoney.dto.response.DeleteEventResponse;
import com.hana4.sonjumoney.dto.response.EventResponse;
import com.hana4.sonjumoney.service.EventService;
import com.hana4.sonjumoney.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Events", description = "일정 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
	private final EventService eventService;

	@PostMapping()
	public ResponseEntity<EventResponse> addEvent(Authentication authentication,
		@RequestParam(value = "family_id") Long familyId,
		@RequestBody AddEventRequest addEventRequest) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		EventResponse eventResponse = eventService.addEvent(userId, familyId, addEventRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
	}

	@GetMapping()
	public ResponseEntity<List<EventResponse>> getAllEvents(Authentication authentication,
		@RequestParam(value = "family_id") Long familyId,
		@RequestParam(required = false) Integer year,
		@RequestParam(required = false) Integer month) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		int getYear = (year == null) ? LocalDate.now().getYear() : year;
		int getMonth = (month == null) ? LocalDate.now().getMonthValue() : month;
		List<EventResponse> eventResponseList = eventService.getAllEvents(userId, familyId, getYear, getMonth);
		return ResponseEntity.ok().body(eventResponseList);
	}

	@GetMapping("/{event_id}")
	public ResponseEntity<EventResponse> getEvent(Authentication authentication,
		@PathVariable(value = "event_id") Long eventId) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		EventResponse eventResponse = eventService.getEvent(userId, eventId);
		return ResponseEntity.ok().body(eventResponse);
	}

	@PatchMapping("/{event_id}")
	public ResponseEntity<EventResponse> updateEvent(Authentication authentication,
		@PathVariable(value = "event_id") Long eventId,
		@RequestBody UpdateEventRequest updateEventRequest) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		EventResponse eventResponse = eventService.updateEvent(userId, eventId, updateEventRequest);
		return ResponseEntity.ok().body(eventResponse);
	}

	@DeleteMapping("/{event_id}")
	public ResponseEntity<DeleteEventResponse> deleteEvent(Authentication authentication,
		@PathVariable(value = "event_id") Long eventId) {
		Long userId = AuthenticationUtil.getUserId(authentication);
		return ResponseEntity.ok().body(eventService.deleteEvent(userId, eventId));
	}

}
