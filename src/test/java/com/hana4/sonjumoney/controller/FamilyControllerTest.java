package com.hana4.sonjumoney.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.dto.InviteChildDto;
import com.hana4.sonjumoney.dto.InviteUserDto;
import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.CreateFamilyResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.UserRepository;

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
	@Autowired
	UserRepository userRepository;

	@Test
	@DisplayName("가족 생성 api 테스트")
	void addFamilyTest() throws Exception {
		List<InviteUserDto> inviteUsers = new ArrayList<>();
		List<InviteChildDto> inviteChildren = new ArrayList<>();
		User user = userRepository.findById(3L).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		inviteUsers.add(new InviteUserDto("01012341234", "아빠"));
		inviteChildren.add(new InviteChildDto(user.getId(), user.getUsername()));
		CreateFamilyRequest request = CreateFamilyRequest.builder()
			.familyName("OO이네")
			.role("아들")
			.addMembers(inviteUsers)
			.addChildren(inviteChildren)
			.build();
		String api = "/api/v1/families";
		ResultActions resultActions = mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isCreated());
		CreateFamilyResponse response = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(),
			CreateFamilyResponse.class);
		Long familyId = response.familyId();
		Family family = familyRepository.findById(familyId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		assertEquals(family.getFamilyName(), "OO이네");
		List<Member> members = memberRepository.findByFamilyId(familyId);
		System.out.println(members.get(1).getUser().getId());

	}

	@Test
	@DisplayName("가족 목록 조회 테스트")
	void findFamiliesTest() throws Exception {
		String api = "/api/v1/families";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("가족 구성원 조회 테스트 : 모두")
	void findFamilyMembersAllTest() throws Exception {
		long familyId = 1L;
		String api = "/api/v1/families/" + familyId + "/members?range=ALL";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.family_id").exists())
			.andExpect(jsonPath("$.family_name").exists())
			.andExpect(jsonPath("$.members").exists())
			.andDo(print());
	}

	@Test
	@DisplayName("가족 구성원 조회 테스트 : 본인 제외")
	void findFamilyMembersExceptUserTest() throws Exception {
		long familyId = 1L;
		String api = "/api/v1/families/" + familyId + "/members?range=EXCEPTME";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.family_id").exists())
			.andExpect(jsonPath("$.family_name").exists())
			.andExpect(jsonPath("$.members").exists())
			/* members response에 userId가 없는지 검증 */
			.andExpect(result -> {
				String response = result.getResponse().getContentAsString();
				JsonNode jsonNode = objectMapper.readTree(response);
				JsonNode members = jsonNode.path("members");
				for (JsonNode member : members) {
					String responseId = member.path("user_id").toString();
					assertThat(loginUserId).isNotEqualTo(responseId);
				}
			})
			.andDo(print());
	}

	@Test
	@DisplayName("가족 구성원 조회 테스트 : 자식만")
	void findFamilyMembersOnlyChildTest() throws Exception {
		long familyId = 1L;
		String api = "/api/v1/families/" + familyId + "/members?range=CHILDREN";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			/* members response에 memberRole이 아들과 딸만 있는지 검증 */
			.andExpect(result -> {
				String response = new String(result.getResponse().getContentAsString().getBytes(
					StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
				JsonNode jsonNode = objectMapper.readTree(response);
				JsonNode members = jsonNode.path("members");
				for (JsonNode member : members) {
					String responseRole = member.path("member_role").toString().replace("\"", "");
					assertThat(responseRole).satisfiesAnyOf(v -> assertThat(v).isEqualTo("아들"),
						v -> assertThat(v).isEqualTo("딸"));
				}
			})
			.andDo(print());
	}
}
