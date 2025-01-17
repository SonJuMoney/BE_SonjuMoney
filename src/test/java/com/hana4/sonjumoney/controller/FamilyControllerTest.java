package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.junit.jupiter.api.Assertions.*;

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
import com.hana4.sonjumoney.dto.InviteUserDto;
import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.CreateFamilyResponse;
import com.hana4.sonjumoney.repository.FamilyRepository;

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

	@Test
	@DisplayName("가족 생성 api 테스트")
	void addFamilyTest() throws Exception {
		List<InviteUserDto> inviteUsers = new ArrayList<>();
		inviteUsers.add(new InviteUserDto(2L, "아빠"));
		CreateFamilyRequest request = CreateFamilyRequest.builder()
			.familyName("OO이네")
			.role("아들")
			.addMembers(inviteUsers)
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
	}
}