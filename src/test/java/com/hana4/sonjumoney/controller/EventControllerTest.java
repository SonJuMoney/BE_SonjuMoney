package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.EventCategory;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.domain.enums.NotifyStatus;
import com.hana4.sonjumoney.dto.request.EventAddRequest;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	FamilyRepository familyRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ObjectMapper objectMapper;

	@BeforeEach
	public void before() throws Exception {

		Family family = Family.builder()
			.familyName("준용이네")
			.build();
		familyRepository.save(family);

		User user1 = userRepository.findById(1L).get();
		User user2 = userRepository.findById(2L).get();

		Member member1 = Member.builder()
			.family(family)
			.memberRole(MemberRole.FATHER)
			.user(user1)
			.build();
		memberRepository.save(member1);
		Member member2 = Member.builder()
			.family(family)
			.memberRole(MemberRole.MOTHER)
			.user(user2)
			.build();
		memberRepository.save(member2);

	}

	@Test
	@DisplayName("일정 등록 테스트")
	void addEventTest() throws Exception {
		List<Long> memberIds = memberRepository.findAll().stream()
			.map(Member::getId)
			.toList();

		EventAddRequest eventAddRequest = EventAddRequest.builder()
			.eventCategory(EventCategory.MEMORIAL)
			.eventName("결혼")
			.memberId(memberIds)
			.startDate(LocalDate.of(2025, 1, 6))
			.endDate(LocalDate.of(2025, 1, 6))
			.notifyStatus(NotifyStatus.REGISTERED)
			.build();
		mockMvc.perform(post("/api/events")
				.param("familyId", "1")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventAddRequest)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.event_id").isNotEmpty())
			.andExpect(jsonPath("$.event_category").value("MEMORIAL"))
			.andExpect(jsonPath("$.event_name").value("결혼"))
			.andExpect(jsonPath("$.start_date").value("2025-01-06"))
			.andExpect(jsonPath("$.end_date").value("2025-01-06"))
			.andExpect(jsonPath("$.event_participants.length()").value(2));
	}

}

