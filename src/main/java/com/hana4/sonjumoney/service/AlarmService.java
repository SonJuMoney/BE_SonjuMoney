package com.hana4.sonjumoney.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.Alarm;
import com.hana4.sonjumoney.dto.AlarmContentDto;
import com.hana4.sonjumoney.dto.AlarmResultDto;
import com.hana4.sonjumoney.dto.response.AlarmResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AlarmRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {

	private final AlarmRepository alarmRepository;
	private final UserRepository userRepository;
	private static final int PAGE_SIZE = 30;

	public AlarmResponse getAlarms(Long userId, Integer page) {
		try {
			PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
			List<Alarm> alarms = alarmRepository.findByUserIdOrderByIdDesc(userId, pageRequest);
			if (alarms.isEmpty()) {
				return AlarmResponse.builder()
					.isSuccess(true)
					.code(200)
					.message("요청성공")
					.result(AlarmResultDto.builder()
						.hasNext(false)
						.page(page)
						.contents(new ArrayList<>())
						.build())
					.build();
			}
			List<AlarmContentDto> contents = new ArrayList<>();
			Boolean hasNext = alarmRepository.hasNext(userId, alarms.get(alarms.size() - 1).getId());
			for (Alarm alarm : alarms) {
				contents.add(AlarmContentDto.of(
					alarm.getId(),
					alarm.getAlarmStatus(),
					alarm.getMessage(),
					alarm.getLinkId(),
					alarm.getCreatedAt()
				));
			}
			AlarmResultDto result = AlarmResultDto.of(
				hasNext,
				page,
				contents
			);

			return AlarmResponse.of(
				true,
				200,
				"요청성공",
				result
			);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

}
