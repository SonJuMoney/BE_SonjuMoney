package com.hana4.sonjumoney.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.dto.request.SignUpRequest;
import com.hana4.sonjumoney.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	String authId = "test1";
	String password = "1234";
	@Autowired
	private UserRepository userRepository;

	@Test
	void getDuplicationTest() throws Exception {
		String url = "/api/auth/id-duplication";
		String notFoundedId = "test0";

		// 아이디가 중복일 때는 true 반환
		mockMvc.perform(
				get(url).contentType(MediaType.APPLICATION_JSON)
					.param("id", authId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.duplication", is(true)));

		// 없는 아이디를 넣으면 false 반환
		mockMvc.perform(
				get(url).contentType(MediaType.APPLICATION_JSON)
					.param("id", notFoundedId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.duplication", is(false)));
	}

	@Test
	void signUpTest() throws Exception {

		class Test {
			final String url = "/api/auth/sign-up";
			final String password = "test123!@#";
			final String name = "테스트유저0";

			String authId = "test0";
			String residentNumber = "010101-1112233";
			String pin = "123456";
			String phone = "01011112222";

			SignUpRequest signUpRequest = new SignUpRequest(authId, password, name, residentNumber, pin, phone);

			void setNewRequest() {
				signUpRequest = new SignUpRequest(authId, password, name, residentNumber, pin, phone);
			}

			void goodCase() throws Exception {
				mockMvc.perform(
						post(url).contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(signUpRequest)))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON));

				User user = userRepository.findByAuthId(authId).orElseThrow();
				userRepository.delete(user);
			}

			void badCase() throws Exception {
				System.out.println("test!--------------");
				mockMvc.perform(
						post(url).contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(signUpRequest)))
					.andExpect(status().is(anyOf(
						is(400),
						is(401),
						is(403),
						is(404),
						is(409),
						is(500)
					)))
					.andExpect(content().contentType(MediaType.APPLICATION_JSON));
			}

		}

		// Case1 - 정상
		Test test1 = new Test();
		test1.goodCase();

		// // Case2 - 아이디 중복
		Test test2 = new Test();
		test2.authId = authId;
		test2.setNewRequest();
		test2.badCase();
		//
		// Case3 - 주민등록번호 오류
		Test test3 = new Test();
		test3.residentNumber = "010101-000333322";
		test3.setNewRequest();
		test3.badCase();

		// Case4 - pin번호 오류
		Test test4 = new Test();
		test4.pin = "12345678";
		test4.setNewRequest();
		test4.badCase();

		// Case5 - 전화번호 오류
		Test test5 = new Test();
		test5.phone = "010-1111-2222";
		test5.setNewRequest();
		test5.badCase();

	}

}
