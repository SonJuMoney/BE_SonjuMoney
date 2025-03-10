package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Alarm;
import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.repository.AlarmRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlarmControllerTest extends ControllerTest {

	@Autowired
	private AlarmRepository alarmRepository;

	@Test
	void getAlarmsTest() throws Exception {
		String api = "/api/v1/alarms";

		int page = 0;

		mockMvc.perform(get(api + "?page=" + page)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.hasNext").value(true))
			.andExpect(jsonPath("$.result.contents.length()").value(30))
			.andDo(print());

		page += 1;

		mockMvc.perform(get(api + "?page=" + page)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.hasNext").value(false))
			.andExpect(jsonPath("$.result.contents.length()").value(30))
			.andDo(print());
	}

	@Test
	void updateAlarmTest() throws Exception {
		String api = "/api/v1/alarms";
		final Long ID = 1L;

		mockMvc.perform(patch(api + "/" + ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk());
		Alarm alarm = alarmRepository.findById(ID).orElseThrow();
		alarm.changeStatusReceived();
		alarmRepository.save(alarm);
	}

	@Test
	void getAlarmStatusTest() throws Exception {
		String api = "/api/v1/alarms/status/";
		AlarmStatus status = AlarmStatus.RECEIVED;

		mockMvc.perform(get(api + status)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.is_exist").value(true));

		for (Alarm alarm : alarmRepository.findAll()) {
			alarm.changeStatusToChecked();
			alarmRepository.save(alarm);
		}

		mockMvc.perform(get(api + status)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.is_exist").value(false));
		
		for (Alarm alarm : alarmRepository.findAll()) {
			alarm.changeStatusReceived();
			alarmRepository.save(alarm);
		}
	}
}
