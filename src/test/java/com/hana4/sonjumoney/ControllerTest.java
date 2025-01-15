package com.hana4.sonjumoney;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.dto.request.SignInRequest;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ControllerTest {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	protected String accessToken;

	@BeforeEach
	void setUp() throws Exception {
		SignInRequest signInRequest = new SignInRequest("test1", "1234");
		MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);
		accessToken = responseMap.get("access_token");
	}
}

