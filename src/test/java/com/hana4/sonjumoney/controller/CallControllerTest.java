package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.hana4.sonjumoney.ControllerTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CallControllerTest extends ControllerTest {

	@Test
	void getRecommendationsTest() throws Exception {
		String api = "/api/v1/calls/recommendations";
		mockMvc.perform(get(api)
				.header("Authorization", "Bearer " + accessToken)
				.param("target_id", "2")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].topic").isNotEmpty())
			.andExpect(jsonPath("$[1].topic").isNotEmpty())
			.andExpect(jsonPath("$[2].topic").isNotEmpty())
			.andExpect(jsonPath("$[3].topic").isNotEmpty())
			.andExpect(jsonPath("$[4].topic").isNotEmpty());
	}
}
