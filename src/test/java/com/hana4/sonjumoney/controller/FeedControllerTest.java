package com.hana4.sonjumoney.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
import com.hana4.sonjumoney.domain.Comment;
import com.hana4.sonjumoney.domain.Feed;
import com.hana4.sonjumoney.domain.FeedContent;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.enums.FeedType;
import com.hana4.sonjumoney.dto.request.CreateFeedRequest;
import com.hana4.sonjumoney.dto.request.PostFeedCommentRequest;
import com.hana4.sonjumoney.dto.response.CreateFeedResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.CommentRepository;
import com.hana4.sonjumoney.repository.FeedContentRepository;
import com.hana4.sonjumoney.repository.FeedRepository;
import com.hana4.sonjumoney.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FeedControllerTest extends ControllerTest {

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private FeedContentRepository feedContentRepository;
	private Long feedId;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CommentRepository commentRepository;

	@Test
	@Order(1)
	void addFeedTest() throws Exception {
		// given
		CreateFeedRequest request = new CreateFeedRequest(1L, "즐거운 여행~");
		MockMultipartFile image1 = new MockMultipartFile(
			"files",
			"feed-test-image1.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {1}
		);
		MockMultipartFile image2 = new MockMultipartFile(
			"files",
			"feed-test-image2.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {2}
		);
		MockMultipartFile video3 = new MockMultipartFile(
			"files",
			"feed-test-video3.mp4",
			"video/mp4",
			new byte[] {3}
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
				.file(video3)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("피드 등록이 완료되었습니다."));
		CreateFeedResponse createFeedResponse = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), CreateFeedResponse.class);
		feedId = createFeedResponse.feedId();
		Feed savedFeed = feedRepository.findById(feedId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		List<FeedContent> feedContents = feedContentRepository.findAllByFeed(savedFeed);
		List<String> urls = feedContents.stream().map(FeedContent::getContentUrl).toList();
		for (FeedContent content : feedContents) {
			assertNotNull(content.getContentUrl(), "URL should not be null");
		}
		Files.delete(Paths.get(urls.get(2)));
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
			new byte[] {1}
		);
		MockMultipartFile image2 = new MockMultipartFile(
			"images",
			"feed-test-image2.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {2}
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

	@Test
	@Order(3)
	void getFeedsTest() throws Exception {
		Feed feed = Feed.builder()
			.member(memberRepository.findByUserIdAndFamilyId(1L, 1L).orElseThrow())
			.allowance(null)
			.receiverId(null)
			.contentExist(true)
			.likes(0)
			.feedMessage("ㅋㅋ")
			.feedType(FeedType.NORMAL)
			.build();
		feedRepository.saveAndFlush(feed);

		String api = "/api/feeds";

		mockMvc.perform(get(api).header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.param("family_id", "1")
				.param("page", "0"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.contents").isNotEmpty());
	}

	@Test
	@Order(4)
	void postFeedLikeTest() throws Exception {
		Feed feed = Feed.builder()
			.member(memberRepository.findByUserIdAndFamilyId(1L, 1L).orElseThrow())
			.allowance(null)
			.receiverId(null)
			.contentExist(true)
			.likes(0)
			.feedMessage("ㅋㅋ")
			.feedType(FeedType.NORMAL)
			.build();
		Feed result = feedRepository.saveAndFlush(feed);
		Long feedId = result.getId();
		String api = "/api/feeds/" + feedId + "/likes";

		mockMvc.perform(post(api).header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		Feed updated = feedRepository.findById(feedId).orElseThrow();
		Assertions.assertThat(updated.getLikes()).isEqualTo(1);
	}

	@Test
	@Order(5)
	void postFeedCommentTest() throws Exception {
		Feed feed = Feed.builder()
			.member(memberRepository.findByUserIdAndFamilyId(1L, 1L).orElseThrow())
			.allowance(null)
			.receiverId(null)
			.contentExist(true)
			.likes(0)
			.feedMessage("ㅋㅋ")
			.feedType(FeedType.NORMAL)
			.build();
		Feed result = feedRepository.saveAndFlush(feed);
		Long feedId = result.getId();
		String api = "/api/feeds/" + feedId + "/comments";
		String comment = "댓글ㅋㅋ";
		PostFeedCommentRequest request = new PostFeedCommentRequest(comment);
		mockMvc.perform(post(api).header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
		Comment inserted = commentRepository.findAllByFeed(result).get(0);
		Assertions.assertThat(inserted.getMessage()).isEqualTo(comment);
	}

	@Test
	@Order(6)
	void DeleteCommentTest() throws Exception {
		Member member = memberRepository.findByUserIdAndFamilyId(1L, 1L).orElseThrow();
		String commentMessage = "ㅠㅠ";
		Feed feed = Feed.builder()
			.member(member)
			.allowance(null)
			.receiverId(null)
			.contentExist(true)
			.likes(0)
			.feedMessage("ㅋㅋ")
			.feedType(FeedType.NORMAL)
			.build();
		Feed insertedFeed = feedRepository.saveAndFlush(feed);
		Comment comment = Comment.builder()
			.feed(insertedFeed)
			.member(member)
			.message(commentMessage)
			.build();
		Comment insertedComment = commentRepository.saveAndFlush(comment);

		String api = "/api/feeds/comments/" + insertedComment.getId();
		mockMvc.perform(delete(api).header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		Assertions.assertThat(commentRepository.findById(insertedComment.getId()).isEmpty());
	}
}
