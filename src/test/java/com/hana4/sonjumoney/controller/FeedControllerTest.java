package com.hana4.sonjumoney.controller;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.dto.request.CreateFeedRequest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class FeedControllerTest extends ControllerTest {

	@Test
	void addFeedTest()throws Exception {
		// given
		CreateFeedRequest request = new CreateFeedRequest(1L, "즐거운 여행~");
		MockMultipartFile image1 = new MockMultipartFile(
			"images",
			"feed-test-image1.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {}
		);
		MockMultipartFile image2 = new MockMultipartFile(
			"images",
			"feed-test-image2.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {}
		);
		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);

		String api = "/api/feeds";
		mockMvc.perform(multipart(api)
				.file(image1)
				.file(image2)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("피드 등록이 완료되었습니다."));

	}
}