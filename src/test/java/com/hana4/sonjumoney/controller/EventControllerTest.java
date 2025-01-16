package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import com.hana4.sonjumoney.domain.enums.EventCategory;
import com.hana4.sonjumoney.domain.enums.NotifyStatus;
import com.hana4.sonjumoney.dto.request.EventAddRequest;

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
		EventAddRequest eventAddRequest = EventAddRequest.builder()
			.eventCategory(EventCategory.MEMORIAL)
			.eventName("결혼")
			.memberId(List.of(1L, 2L))
			.startDate(LocalDate.of(2025, 1, 6))
			.endDate(LocalDate.of(2025, 1, 6))
			.notifyStatus(NotifyStatus.REGISTERED)
			.build();
		mockMvc.perform(post("/api/events")
				.param("familyId", "1")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventAddRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.event_id").isNotEmpty())
			.andExpect(jsonPath("$.event_category").value("MEMORIAL"))
			.andExpect(jsonPath("$.event_name").value("결혼"))
			.andExpect(jsonPath("$.start_date").value("2025-01-06"))
			.andExpect(jsonPath("$.end_date").value("2025-01-06"))
			.andExpect(jsonPath("$.event_participants[0].member_id").value(1))
			.andExpect(jsonPath("$.event_participants[1].member_id").value(2))
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("일정 목록 조회 테스트(기본값: 현재 연도, 현재 월)")
	public void getAllEventsDeafaulDateTest() throws Exception {
		mockMvc.perform(get("/api/events")
				.param("familyId", "1")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			//날짜순 첫번째 일정
			.andExpect(jsonPath("$[0].event_id").value(2))
			.andExpect(jsonPath("$[0].event_category").value("DINING"))
			.andExpect(jsonPath("$[0].event_name").value("고기 먹는날 ~"))
			.andExpect(jsonPath("$[0].start_date").value("2025-01-19"))
			.andExpect(jsonPath("$[0].end_date").value("2025-01-19"))
			.andExpect(jsonPath("$[0].event_participants[0].participation_id").value(3))
			.andExpect(jsonPath("$[0].event_participants[1].participation_id").value(4))
			//두번째 일정
			.andExpect(jsonPath("$[1].event_id").value(1))
			.andExpect(jsonPath("$[1].event_category").value("TRAVEL"))
			.andExpect(jsonPath("$[1].event_name").value("우리 가족 여행"))
			.andExpect(jsonPath("$[1].start_date").value("2025-01-25"))
			.andExpect(jsonPath("$[1].end_date").value("2025-01-31"))
			.andExpect(jsonPath("$[1].event_participants[0].participation_id").value(1))
			.andExpect(jsonPath("$[1].event_participants[1].participation_id").value(2))
			.andDo(print());

	}

	@Test
	@Transactional
	@DisplayName("일정 목록 조회 테스트(조회 연도, 조회 월)")
	public void getAllEventsGetDateTest() throws Exception {
		mockMvc.perform(get("/api/events")
				.param("familyId", "1")
				.param("year", "2025")
				.param("month", "2")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].event_id").value(3))
			.andExpect(jsonPath("$[0].event_category").value("MEMORIAL"))
			.andExpect(jsonPath("$[0].event_name").value("결기❤️"))
			.andExpect(jsonPath("$[0].start_date").value("2025-02-01"))
			.andExpect(jsonPath("$[0].end_date").value("2025-02-01"))
			.andExpect(jsonPath("$[0].event_participants[0].participation_id").value(5))
			.andExpect(jsonPath("$[0].event_participants[1].participation_id").value(6))
			.andDo(print());

	}

}


