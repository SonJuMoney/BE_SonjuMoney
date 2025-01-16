package com.hana4.sonjumoney.websocket.handler;

import java.io.IOException;
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
import com.hana4.sonjumoney.websocket.dto.AlarmDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {
	private final ObjectMapper objectMapper;

	private final Set<WebSocketSession> sessions = new HashSet<>();

	private final Map<Long, Set<WebSocketSession>> alarmSessionMap = new HashMap<>();

	public void sendAlarm(AlarmDto alarmDto) {
		Long alarmSessionId = alarmDto.alarmSessionId();
		if (!alarmSessionMap.containsKey(alarmSessionId)) {
			alarmSessionMap.put(alarmSessionId, new HashSet<>());
		}
		Set<WebSocketSession> alarmSession = alarmSessionMap.get(alarmSessionId);
		for (WebSocketSession session : alarmSession) {
			try {
				session.sendMessage(new TextMessage(alarmDto.alarmType()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		// alarmSession.add(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		log.info("payload {}", payload);
		session.sendMessage(message);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session)throws Exception {
		sessions.add(session);
		log.info("session id: " + session.getId() + "session uri: " + session.getUri());
		try {
			session.sendMessage(
				new TextMessage("웹소켓 연결 성공")
			);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void addAlarmSession() {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessions.remove(session);
		alarmSessionMap.values().forEach(sessions -> sessions.remove(session));
	}
}
