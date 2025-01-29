package com.hana4.sonjumoney.websocket.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
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
	private final Map<Long,WebSocketSession> userAlarmSessionMap = new HashMap<>();

	public void sendUserAlarm(SendAlarmDto sendAlarmDto) {
		try {
			log.info("유저별 알림 전송 진입");
			Long userAlarmSessionId = sendAlarmDto.alarmSessionId();
			WebSocketSession session = userAlarmSessionMap.get(userAlarmSessionId);
			log.info("toId: " + userAlarmSessionId + " session: " + session.getId());
			if (session.isOpen()) {
				TextMessage alarmMessage = new TextMessage(objectMapper.writeValueAsString(
					AlarmMessageDto.of(sendAlarmDto.alarmId(), sendAlarmDto.alarmStatus(), sendAlarmDto.alarmType(),
						sendAlarmDto.message(), sendAlarmDto.linkId(), sendAlarmDto.familyId(),
						sendAlarmDto.createdAt())));
				log.info(alarmMessage.getPayload());
				session.sendMessage(alarmMessage);
			} else {
				log.info("세션을 닫습니다.");
				session.close();
			}
		} catch (NullPointerException e) {
			log.error(ErrorCode.NOT_FOUND_OPPONENET.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
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
			session.sendMessage(new TextMessage(userId + " 웹소켓 연결 성공"));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void addAlarmSession() {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessions.remove(session);
		userAlarmSessionMap.values().remove(session);
	}
}
