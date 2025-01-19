package com.hana4.sonjumoney.controller;

import static org.assertj.core.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Feed;
import com.hana4.sonjumoney.dto.request.CreateFeedRequest;
import com.hana4.sonjumoney.dto.response.CreateFeedResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FeedRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FeedControllerTest extends ControllerTest {

	@Autowired
	private FeedRepository feedRepository;

	private Long feedId;

	@Test
	@Order(1)
	void addFeedTest() throws Exception {
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
		ResultActions resultActions = mockMvc.perform(multipart(api)
				.file(image1)
				.file(image2)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("피드 등록이 완료되었습니다."));
		CreateFeedResponse createFeedResponse = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), CreateFeedResponse.class);
		feedId = createFeedResponse.feedId();
	}

	@Test
	@Order(2)
	void deleteFeedTest() throws Exception {
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

		ResultActions resultActions = mockMvc.perform(multipart("/api/feeds")
				.file(image1)
				.file(image2)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("피드 등록이 완료되었습니다."));
		CreateFeedResponse createFeedResponse = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), CreateFeedResponse.class);
		feedId = createFeedResponse.feedId();

		// when
		String api = "/api/feeds/" + feedId;
		mockMvc.perform(delete(api)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk());
		// then
		Assertions.assertThat(feedRepository.findById(feedId).isEmpty());
	}
}