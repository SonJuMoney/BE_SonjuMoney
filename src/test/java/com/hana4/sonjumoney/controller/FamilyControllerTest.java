package com.hana4.sonjumoney.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.dto.InviteUserDto;
import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.CreateFamilyResponse;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FamilyControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	FamilyRepository familyRepository;
	@Autowired
	MemberRepository memberRepository;

	@Test
	@DisplayName("가족 생성 api 테스트")
	void addFamilyTest() throws Exception {
		List<InviteUserDto> inviteUsers = new ArrayList<>();
		List<Long> inviteChildren = new ArrayList<>();

		inviteUsers.add(new InviteUserDto("01012341234", "아빠"));
		inviteChildren.add(3L);
		CreateFamilyRequest request = CreateFamilyRequest.builder()
			.familyName("OO이네")
			.role("아들")
			.addMembers(inviteUsers)
			.addChildren(inviteChildren)
			.build();
		String api = "/api/families";
		ResultActions resultActions = mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isCreated());
		CreateFamilyResponse response = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(),
			CreateFamilyResponse.class);
		Long familyId = response.familyId();
		assertEquals(familyRepository.findById(familyId).get().getFamilyName(), "OO이네");
		List<Member> members = memberRepository.findByFamilyId(familyId);
		System.out.println(members.get(1).getUser().getId());

	}

	@Test
	@DisplayName("가족 목록 조회 테스트")
	void findFamiliesTest() throws Exception {
		String api = "/api/families";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
