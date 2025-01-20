package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Relationship;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.dto.request.AuthPinRequest;
import com.hana4.sonjumoney.dto.request.SignUpChildRequest;
import com.hana4.sonjumoney.repository.RelationshipRepository;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.security.util.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthWithTokenControllerTest extends ControllerTest {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RelationshipRepository relationshipRepository;

	@Test
	void validatePinTest() throws Exception {
		String pin = userRepository.findById(jwtUtil.getUserId(accessToken)).get().getPin();

		String url = "/api/auth/pin";
		AuthPinRequest pinRequest = new AuthPinRequest(pin);
		mockMvc.perform(post(url)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pinRequest)))
			.andExpect(status().isOk());

		AuthPinRequest notMatchPinRequest = new AuthPinRequest("1234567");
		mockMvc.perform(post(url)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(notMatchPinRequest)))
			.andExpect(status().is4xxClientError());
	}

	@Test
	void SignUpChildTest() throws Exception {
		String api = "/api/auth/sign-up-child";
		String authId = "child0";
		String name = "테스트자식0";
		String residentNumber = "2412314123123";
		SignUpChildRequest request = new SignUpChildRequest(authId, name, residentNumber);

		mockMvc.perform(
				post(api).contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		User child = userRepository.findByAuthId(authId).orElseThrow();
		Relationship relationship = relationshipRepository.findByChildId(child.getId());
		relationshipRepository.delete(relationship);
		userRepository.delete(child);
	}
}
