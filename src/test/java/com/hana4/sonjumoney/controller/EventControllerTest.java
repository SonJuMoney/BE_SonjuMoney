package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.enums.AllDayStatus;
import com.hana4.sonjumoney.domain.enums.EventCategory;
import com.hana4.sonjumoney.dto.request.AddEventRequest;
import com.hana4.sonjumoney.dto.request.UpdateEventRequest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@Transactional
	@DisplayName("일정 등록 테스트")
	void addEventTest() throws Exception {
		AddEventRequest addEventRequest = AddEventRequest.builder()
			.eventCategory(EventCategory.MEMORIAL)
			.eventName("결혼")
			.memberId(List.of(1L, 2L))
			.startDateTime(LocalDateTime.of(2025, 1, 6, 0, 0, 0))
			.endDateTime(LocalDateTime.of(2025, 1, 6, 0, 0, 0))
			.allDayStatus(AllDayStatus.ALL_DAY)
			.build();
		mockMvc.perform(post("/api/events")
				.param("family_id", "1")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(addEventRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.event_id").isNotEmpty())
			.andExpect(jsonPath("$.event_category").value("MEMORIAL"))
			.andExpect(jsonPath("$.event_name").value("결혼"))
			.andExpect(jsonPath("$.start_date_time").value("2025-01-06T00:00:00"))
			.andExpect(jsonPath("$.end_date_time").value("2025-01-06T00:00:00"))
			.andExpect(jsonPath("$.all_day_status").value("ALL_DAY"))
			.andExpect(jsonPath("$.event_participants[0].user_name").value("계좌 있는 놈"))
			.andExpect(jsonPath("$.event_participants[1].user_name").value("계좌 없는 놈"))
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("일정 목록 조회 테스트(기본값: 현재 연도, 현재 월)")
	public void getAllEventsDefaultDateTest() throws Exception {
		mockMvc.perform(get("/api/events")
				.param("family_id", "1")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			//날짜순 첫번째 일정
			.andExpect(jsonPath("$[0].event_id").value(2))
			.andExpect(jsonPath("$[0].event_category").value("DINING"))
			.andExpect(jsonPath("$[0].event_name").value("고기 먹는날 ~"))
			.andExpect(jsonPath("$[0].start_date_time").value("2025-01-19T00:00:00"))
			.andExpect(jsonPath("$[0].end_date_time").value("2025-01-19T23:59:59"))
			.andExpect(jsonPath("$[0].event_participants[0].user_name").value("계좌 있는 놈"))
			.andExpect(jsonPath("$[0].event_participants[1].user_name").value("계좌 없는 놈"))
			//두번째 일정
			.andExpect(jsonPath("$[1].event_id").value(1))
			.andExpect(jsonPath("$[1].event_category").value("TRAVEL"))
			.andExpect(jsonPath("$[1].event_name").value("우리 가족 여행"))
			.andExpect(jsonPath("$[1].start_date_time").value("2025-01-25T10:00:00"))
			.andExpect(jsonPath("$[1].end_date_time").value("2025-01-31T23:59:59"))
			.andExpect(jsonPath("$[1].event_participants[0].user_name").value("계좌 있는 놈"))
			.andExpect(jsonPath("$[1].event_participants[1].user_name").value("계좌 없는 놈"))
			.andDo(print());

	}

	@Test
	@Transactional
	@DisplayName("일정 목록 조회 테스트(조회 연도, 조회 월)")
	public void getAllEventsGetDateTest() throws Exception {
		mockMvc.perform(get("/api/events")
				.param("family_id", "1")
				.param("year", "2025")
				.param("month", "2")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].event_id").value(3))
			.andExpect(jsonPath("$[0].event_category").value("MEMORIAL"))
			.andExpect(jsonPath("$[0].event_name").value("결기❤️"))
			.andExpect(jsonPath("$[0].start_date_time").value("2025-02-01T00:00:00"))
			.andExpect(jsonPath("$[0].end_date_time").value("2025-02-01T23:59:59"))
			.andExpect(jsonPath("$[0].event_participants[0].user_name").value("계좌 있는 놈"))
			.andExpect(jsonPath("$[0].event_participants[1].user_name").value("계좌 없는 놈"))
			.andDo(print());

	}

	@Test
	@Transactional
	@DisplayName("일정 조회 테스트")
	public void getEventTest() throws Exception {
		mockMvc.perform(get("/api/events/{event_id}", 2)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.event_id").value(2))
			.andExpect(jsonPath("$.event_category").value("DINING"))
			.andExpect(jsonPath("$.start_date_time").value("2025-01-19T00:00:00"))
			.andExpect(jsonPath("$.end_date_time").value("2025-01-19T23:59:59"))
			.andExpect(jsonPath("$.event_participants[0].user_name").value("계좌 있는 놈"))
			.andExpect(jsonPath("$.event_participants[1].user_name").value("계좌 없는 놈"))
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("일정 목록 수정 테스트")
	public void updateEventTest() throws Exception {
		UpdateEventRequest updateEventRequest = UpdateEventRequest.builder()
			.eventCategory(EventCategory.DINING)
			.eventName("치킨 먹는날!")
			.memberId(List.of(1L, 2L))
			.startDateTime(LocalDateTime.of(2025, 1, 19, 12, 0, 0))
			.endDateTime(LocalDateTime.of(2025, 1, 19, 23, 59, 59))
			.allDayStatus(AllDayStatus.ALL_DAY)
			.build();
		mockMvc.perform(patch("/api/events/{event_id}", 2)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateEventRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.event_id").isNotEmpty())
			.andExpect(jsonPath("$.event_category").value("DINING"))
			.andExpect(jsonPath("$.event_name").value("치킨 먹는날!"))
			.andExpect(jsonPath("$.start_date_time").value("2025-01-19T12:00:00"))
			.andExpect(jsonPath("$.end_date_time").value("2025-01-19T23:59:59"))
			.andExpect(jsonPath("$.event_participants[0].user_name").value("계좌 있는 놈"))
			.andExpect(jsonPath("$.event_participants[1].user_name").value("계좌 없는 놈"))
			.andDo(print());
	}

}


