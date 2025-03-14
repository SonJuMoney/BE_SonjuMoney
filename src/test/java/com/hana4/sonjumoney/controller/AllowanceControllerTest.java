package com.hana4.sonjumoney.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Alarm;
import com.hana4.sonjumoney.domain.Allowance;
import com.hana4.sonjumoney.domain.Feed;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.domain.enums.AlarmType;
import com.hana4.sonjumoney.dto.SendAlarmDto;
import com.hana4.sonjumoney.dto.request.SendAllowanceRequest;
import com.hana4.sonjumoney.dto.request.SendThanksRequest;
import com.hana4.sonjumoney.dto.request.SignInRequest;
import com.hana4.sonjumoney.dto.response.SendAllowanceResponse;
import com.hana4.sonjumoney.dto.response.SendThanksResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AlarmRepository;
import com.hana4.sonjumoney.repository.AllowanceRepository;
import com.hana4.sonjumoney.repository.FeedRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.security.util.JwtUtil;
import com.hana4.sonjumoney.service.FeedService;
import com.hana4.sonjumoney.util.AuthenticationUtil;
import com.hana4.sonjumoney.websocket.handler.AlarmHandler;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AllowanceControllerTest extends ControllerTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AllowanceRepository allowanceRepository;

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private AlarmRepository alarmRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private FeedService feedService;

	@MockBean
	private AlarmHandler alarmHandler;

	@Test
	@DisplayName("용돈 보내기 기능 테스트")
	void sendAllowanceTest() throws Exception {
		// given
		doNothing().when(alarmHandler).sendUserAlarm(any(SendAlarmDto.class));
		SendAllowanceRequest request = new SendAllowanceRequest(3L, 5000L, "용돈 잘 쓰렴");
		MockMultipartFile image = new MockMultipartFile(
			"file",
			"test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {}
		);
		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);
		String api = "/api/v1/allowances";
		ResultActions resultActions = mockMvc.perform(multipart(api)
				.file(image)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("송금을 완료했습니다."));
		Alarm alarm = alarmRepository.findLatestAlarmByUserIdAndAlarmStatus(3L, AlarmStatus.RECEIVED)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		assertThat(alarm.getAlarmType().equals(AlarmType.ALLOWANCE));
		assertThat(alarm.getUser().getId().equals(3L));

		SendAllowanceResponse response = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), SendAllowanceResponse.class);
		List<Feed> savedFeeds = feedRepository.findFeedsByAllowanceId(response.allowanceId());
		for (Feed feed : savedFeeds) {
			feedService.deleteFeedById(jwtUtil.getUserId(accessToken), feed.getId());
		}
		feedRepository.deleteAll(savedFeeds);
	}

	@Test
	@DisplayName("아이에게 용돈 보내기 기능 테스트")
	void sendAllowanceChildTest() throws Exception {
		// given
		doNothing().when(alarmHandler).sendUserAlarm(any(SendAlarmDto.class));
		SendAllowanceRequest request = new SendAllowanceRequest(4L, 5000L, "용돈 잘 쓰렴");
		MockMultipartFile image = new MockMultipartFile(
			"file",
			"test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {}
		);
		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);
		String api = "/api/v1/allowances";
		ResultActions resultActions = mockMvc.perform(multipart(api)
				.file(image)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("송금을 완료했습니다."));
		Alarm alarm = alarmRepository.findLatestAlarmByUserIdAndAlarmStatus(6L, AlarmStatus.RECEIVED)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		assertThat(alarm.getAlarmType().equals(AlarmType.ALLOWANCE));
		assertThat(alarm.getUser().getId().equals(3L));

		SendAllowanceResponse response = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), SendAllowanceResponse.class);
		List<Feed> savedFeeds = feedRepository.findFeedsByAllowanceId(response.allowanceId());
		for (Feed feed : savedFeeds) {
			feedService.deleteFeedById(jwtUtil.getUserId(accessToken), feed.getId());
		}
		feedRepository.deleteAll(savedFeeds);
	}

	@Test
	@DisplayName("용돈 보내기 다른 가족 오류 테스트")
	void sendAllowanceDifferentFamilyExceptionTest() throws Exception {
		// given
		doNothing().when(alarmHandler).sendUserAlarm(any(SendAlarmDto.class));
		SendAllowanceRequest request = new SendAllowanceRequest(5L, 5000L, "용돈 잘 쓰렴");
		MockMultipartFile image = new MockMultipartFile(
			"file",
			"test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {}
		);
		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);
		String api = "/api/v1/allowances";
		ResultActions resultActions = mockMvc.perform(multipart(api)
				.file(image)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("용돈 조회 테스트")
	void getAllowanceTest() throws Exception {
		// given
		Member sender = memberRepository.findById(1L)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
		Member receiver = memberRepository.findById(2L)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
		Allowance allowance = allowanceRepository.save(new Allowance(sender, receiver, 5000L));

		String api = "/api/v1/allowances/" + allowance.getId();
		mockMvc.perform(get(api)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.allowance_id").value(allowance.getId()))
			.andExpect(jsonPath("$.sender_name").value(allowance.getSender().getUser().getUsername()))
			.andExpect(jsonPath("$.amount").value(allowance.getAmount()));

	}

	@Test
	@DisplayName("감사메시지 전송 테스트")
	void createAllowanceThanksTest() throws Exception {
		doNothing().when(alarmHandler).sendUserAlarm(any(SendAlarmDto.class));
		SendAllowanceRequest request = new SendAllowanceRequest(3L, 5000L, "용돈 잘 쓰렴");
		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);
		String api = "/api/v1/allowances";
		ResultActions resultActions = mockMvc.perform(multipart(api)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("송금을 완료했습니다."));

		SendAllowanceResponse sendAllowanceResponse = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), SendAllowanceResponse.class);
		Long allowanceId = sendAllowanceResponse.allowanceId();

		SignInRequest signInRequest = new SignInRequest("test3", "1234");
		MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);
		accessToken = responseMap.get("access_token");

		loginUserId = String.valueOf(responseMap.get("user_id"));
		System.out.println("accessToken:" + accessToken);

		MockMultipartFile video = new MockMultipartFile(
			"file",
			"test-video.mp4",
			"video/mp4",
			new byte[] {}
		);
		SendThanksRequest sendThanksRequest = SendThanksRequest.builder().message("감사합니다!").build();
		data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(sendThanksRequest).getBytes(StandardCharsets.UTF_8)
		);
		api = "/api/v1/allowances/" + allowanceId + "/thanks";
		resultActions = mockMvc.perform(multipart(api)
				.file(video)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("감사 메시지를 전송했습니다."));
		SendThanksResponse response = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), SendThanksResponse.class);
		feedService.deleteFeedById(jwtUtil.getUserId(accessToken), response.feedId());
	}

	@Test
	@DisplayName("감사메시지 null 예외 테스트")
	void sendThanksNullMessageTest() throws Exception {
		doNothing().when(alarmHandler).sendUserAlarm(any(SendAlarmDto.class));
		SendAllowanceRequest request = new SendAllowanceRequest(3L, 5000L, "용돈 잘 쓰렴");
		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);
		String api = "/api/v1/allowances";
		ResultActions resultActions = mockMvc.perform(multipart(api)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("송금을 완료했습니다."));

		SendAllowanceResponse sendAllowanceResponse = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), SendAllowanceResponse.class);
		Long allowanceId = sendAllowanceResponse.allowanceId();

		SignInRequest signInRequest = new SignInRequest("test3", "1234");
		MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);
		accessToken = responseMap.get("access_token");

		loginUserId = String.valueOf(responseMap.get("user_id"));
		System.out.println("accessToken:" + accessToken);
		MockMultipartFile video = new MockMultipartFile(
			"file",
			"test-video.mp4",
			"video/mp4",
			new byte[] {}
		);
		SendThanksRequest sendThanksRequest = SendThanksRequest.builder().message(null).build();
		data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsString(sendThanksRequest).getBytes(StandardCharsets.UTF_8)
		);
		api = "/api/v1/allowances/" + allowanceId + "/thanks";
		mockMvc.perform(multipart(api)
				.file(video)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message").value("감사메시지는 null일 수 없습니다."));
	}
}