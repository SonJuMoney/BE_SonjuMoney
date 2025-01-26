package com.hana4.sonjumoney.websocket.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.bridge.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.websocket.dto.AlarmMessageDto;
import com.hana4.sonjumoney.dto.SendAlarmDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmHandler extends TextWebSocketHandler {
	private final ObjectMapper objectMapper;

	private final Set<WebSocketSession> sessions = new HashSet<>();
	private final Map<Long, Set<WebSocketSession>> familyAlarmSessionMap = new HashMap<>();
	private final Map<Long, WebSocketSession> memberAlarmSessionMap = new HashMap<>();
	private final Map<Long,WebSocketSession> userAlarmSessionMap = new HashMap<>();

	private final FamilyRepository familyRepository;
	private final MemberRepository memberRepository;

	// TODO: 가족단위는 스케줄링으로 보낼때 다시 구현
	// public void sendFamilyAlarm(SendAlarmDto sendAlarmDto) {
	// 	Long familyAlarmSessionId = sendAlarmDto.alarmSessionId();
	// 	Long userId = sendAlarmDto.senderId();
	// 	Set<WebSocketSession> alarmSession = familyAlarmSessionMap.get(familyAlarmSessionId);
	// 	for (WebSocketSession session : alarmSession) {
	// 		try {
	// 			// 전송자 제외
	// 			if (session.getAttributes().get("userId") != userId) {
	// 				session.sendMessage(new TextMessage(sendAlarmDto.alarmType().getValue()));
	// 			}
	// 		} catch (Exception e) {
	// 			throw new CommonException(ErrorCode.ALARM_SEND_FAILED);
	// 		}
	// 	}
	// }

	public void sendMemberAlarm(SendAlarmDto sendAlarmDto) {
		Long memberAlarmSessionId = sendAlarmDto.alarmSessionId();
		WebSocketSession session = memberAlarmSessionMap.get(memberAlarmSessionId);
		try {
			session.sendMessage(new TextMessage(sendAlarmDto.alarmType().getValue()));
		} catch (Exception e) {
			throw new CommonException(ErrorCode.ALARM_SEND_FAILED);
		}

	}

	public void sendUserAlarm(SendAlarmDto sendAlarmDto) {
		log.info("유저별 알림 전송 진입");
		Long userAlarmSessionId = sendAlarmDto.alarmSessionId();
		WebSocketSession session = userAlarmSessionMap.get(userAlarmSessionId);
		log.info("toId: " + userAlarmSessionId + " session: " + session.getId());
		try {
			TextMessage alarmMessage = new TextMessage(objectMapper.writeValueAsString(
				AlarmMessageDto.of(sendAlarmDto.alarmId(), sendAlarmDto.alarmStatus(), sendAlarmDto.alarmType(),
					sendAlarmDto.message(), sendAlarmDto.linkId(), sendAlarmDto.familyId(), sendAlarmDto.createdAt())));
			log.info(alarmMessage.getPayload());
			session.sendMessage(alarmMessage);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.ALARM_SEND_FAILED);
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		log.info("payload {}", payload);
		for (WebSocketSession session1 : sessions) {
			session1.sendMessage(message);
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		sessions.add(session);

		Long userId = (Long)session.getAttributes().get("userId");
		log.info("userId: " + userId + " session id: " + session.getId() + " session uri: " + session.getUri());
		try {
			userAlarmSessionMap.put(userId, session);
			List<Member> members = memberRepository.findAllByUserId(userId);
			for (Member member : members) {
				Long memberId = member.getId();
				if (!memberAlarmSessionMap.containsKey(memberId)) {
					memberAlarmSessionMap.put(memberId, session);
				}
				Family family = familyRepository.findById(member.getFamily().getId())
					.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
				Long familyId = family.getId();
				if (!familyAlarmSessionMap.containsKey(familyId)) {
					familyAlarmSessionMap.put(familyId, new HashSet<>());
				}
				Set<WebSocketSession> familySession = familyAlarmSessionMap.get(familyId);
				familySession.add(session);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		try {
			session.sendMessage(new TextMessage(userId + " 웹소켓 연결 성공"));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void addAlarmSession() {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessions.remove(session);
		familyAlarmSessionMap.values().forEach(sessions -> sessions.remove(session));
		memberAlarmSessionMap.values().remove(session);
	}
}
