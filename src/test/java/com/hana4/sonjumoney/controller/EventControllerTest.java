package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.EventCategory;
import com.hana4.sonjumoney.domain.enums.Gender;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.domain.enums.NotifyStatus;
import com.hana4.sonjumoney.dto.request.EventAddRequest;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class EventControllerTest {
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

	private String accessToken;

	@BeforeAll
	public void beforeAll() throws Exception {

		Family family = Family.builder()
			.familyName("준용이네")
			.build();
		familyRepository.save(family);

		/*
		User user1 = User.builder()
			.username("유저1")
			.pin("123456")
			.phone("01012345678")
			.authId("user1")
			.gender(Gender.MALE)
			.password("123456")
			.profileLink("profile")
			.residentNum("990101-1000000")
			.build();
		userRepository.save(user1);*/

		User user1 = userRepository.findById(1L).get();

		User user2 = User.builder()
			.username("유저2")
			.pin("123457")
			.phone("01023456789")
			.authId("user2")
			.gender(Gender.FEMALE)
			.password("123456")
			.profileLink("profile")
			.residentNum("990101-2000000")
			.build();
		userRepository.save(user2);

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
			.andExpect(status().isCreated());
	}

}
