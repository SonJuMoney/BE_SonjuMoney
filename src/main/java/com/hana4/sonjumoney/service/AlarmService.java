package com.hana4.sonjumoney.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.Alarm;
import com.hana4.sonjumoney.domain.Event;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.domain.enums.AlarmType;
import com.hana4.sonjumoney.dto.AlarmContentDto;
import com.hana4.sonjumoney.dto.AlarmResultDto;
import com.hana4.sonjumoney.dto.CreateAlarmDto;
import com.hana4.sonjumoney.dto.SendAlarmDto;
import com.hana4.sonjumoney.dto.response.AlarmResponse;
import com.hana4.sonjumoney.dto.response.AlarmStatusResponse;
import com.hana4.sonjumoney.dto.response.UpdateAlarmResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AlarmRepository;
import com.hana4.sonjumoney.repository.EventParticipantRepository;
import com.hana4.sonjumoney.repository.EventRepository;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.websocket.handler.AlarmHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {

	private final AlarmRepository alarmRepository;
	private final MemberRepository memberRepository;
	private final FamilyRepository familyRepository;
	private final EventRepository eventRepository;
	private final EventParticipantRepository eventParticipantRepository;
	private final UserRepository userRepository;
	private final AlarmHandler alarmHandler;
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
					alarm.getAlarmType(),
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

	public UpdateAlarmResponse updateAlarm(Long alarmId) {
		Alarm alarm = alarmRepository.findById(alarmId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

		// 상태 변경 시작
		try {
			alarm.changeStatusToChecked();
			alarmRepository.save(alarm);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		// 상태 변경 완료

		return UpdateAlarmResponse.builder()
			.code(200)
			.message("요청을 성공했습니다.")
			.build();
	}

	public AlarmStatusResponse getAlarmStatus(Long userId, AlarmStatus alarmStatus) {
		List<Alarm> alarms = alarmRepository.findByUserIdAndAlarmStatus(userId, alarmStatus);
		boolean result = !alarms.isEmpty();
		return AlarmStatusResponse.of(result);
	}

	public void createOneOffAlarm(CreateAlarmDto createAlarmDto) {
		AlarmType alarmType = createAlarmDto.alarmType();
		Member sender = memberRepository.findById(createAlarmDto.senderId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

		switch (alarmType) {
			case ALLOWANCE, THANKS, INVITE: {
				User receiver = userRepository.findById(createAlarmDto.alarmSessionId())
					.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
				String message = sender.getMemberRole().getValue() + "님이 " + alarmType.getMessage();

				Alarm alarm = alarmRepository.save(
					new Alarm(receiver, alarmType, createAlarmDto.linkId(), createAlarmDto.familyId(),
						message));
				alarmHandler.sendUserAlarm(SendAlarmDto.from(alarm));
				break;
			}
			case FEED: {
				Family family = familyRepository.findById(createAlarmDto.alarmSessionId())
					.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
				String message = alarmType.getMessage();
				List<Member> members = memberRepository.findByFamilyId(family.getId());

				for (Member member : members) {
					Alarm alarm = alarmRepository.save(
						new Alarm(member.getUser(), alarmType, createAlarmDto.linkId(), createAlarmDto.familyId(),
							message));
					alarmHandler.sendUserAlarm(SendAlarmDto.from(alarm));
				}
				break;
			}
			default: {
				throw new CommonException(ErrorCode.ALARM_SEND_FAILED);
			}
		}
	}

	private String makeEventAlarmMessage(Long eventId) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		return event.getNotifyStatus().getMessage() + event.getEventName() + "가 있어요.";
	}

	// ---------------아래 부분은 추후 추가 구현 예정----------------//
	// TODO: 이건 개별적으로 안하고 한번에 보내게 바꾸기
	public void createEventAlarm(CreateAlarmDto createAlarmDto) {
		AlarmType alarmType = createAlarmDto.alarmType();
		Member sender = memberRepository.findById(createAlarmDto.senderId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
		Event event = eventRepository.findById(createAlarmDto.linkId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		String alarmMessage = makeEventAlarmMessage(createAlarmDto.linkId());

		switch (alarmType) {
			case BIRTHDAY: {
				Family family = familyRepository.findById(event.getId())
					.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
				List<Member> members = memberRepository.findByFamilyId(family.getId());

				for (Member member : members) {
					alarmRepository.save(
						new Alarm(member.getUser(), alarmType, createAlarmDto.linkId(), createAlarmDto.familyId(),
							alarmMessage));
				}
				break;
			}
			case TRAVEL, DINING, MEMORIAL, OTHERS: {
				List<Member> participants = eventParticipantRepository.findMembersByEventId(event.getId());
				participants.remove(sender);
				for (Member participant : participants) {
					alarmRepository.save(
						new Alarm(participant.getUser(), alarmType, createAlarmDto.linkId(), createAlarmDto.familyId(),
							alarmMessage));
				}
			}
			default: {
				throw new CommonException(ErrorCode.WRONG_ALARM_TYPE);
			}
		}

	}

	public void createSavingAlarm(CreateAlarmDto createAlarmDto) {
		AlarmType alarmType = createAlarmDto.alarmType();
		if (!alarmType.equals(AlarmType.SAVINGS)) {
			throw new CommonException(ErrorCode.WRONG_ALARM_TYPE);
		}
		User user = userRepository.findById(createAlarmDto.alarmSessionId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		String alarmMessage = "내일은 " + alarmType.getMessage();
		alarmRepository.save(
			new Alarm(user, alarmType, createAlarmDto.linkId(), createAlarmDto.familyId(), alarmMessage));

	}

}
