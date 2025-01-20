package com.hana4.sonjumoney.controller;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Allowance;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.dto.request.SendAllowanceRequest;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AllowanceRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.websocket.dto.AlarmDto;
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

	@MockBean
	private AlarmHandler alarmHandler;

	@Test
	@DisplayName("용돈 보내기 기능 테스트")
	void sendAllowanceTest() throws Exception {
		// given
		doNothing().when(alarmHandler).sendMemberAlarm(any(AlarmDto.class));
		SendAllowanceRequest request = new SendAllowanceRequest(3L, 5000L, "용돈 잘 쓰렴");
		MockMultipartFile image = new MockMultipartFile(
			"image",
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
		String api = "/api/allowances";
		mockMvc.perform(multipart(api)
				.file(image)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("송금을 완료했습니다."));

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

		String api = "/api/allowances/" + allowance.getId();
		mockMvc.perform(get(api)
				.header("Authorization", "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.allowance_id").value(allowance.getId()))
			.andExpect(jsonPath("$.sender_name").value(allowance.getSender().getUser().getUsername()))
			.andExpect(jsonPath("$.amount").value(allowance.getAmount()));


	}
}