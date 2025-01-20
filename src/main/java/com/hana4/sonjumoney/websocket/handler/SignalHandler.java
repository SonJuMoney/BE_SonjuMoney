package com.hana4.sonjumoney.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalHandler extends TextWebSocketHandler {
	private final ObjectMapper objectMapper;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
		// WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		log.info("[ws] Session has been closed" + closeStatus + " " + session);
	}
}
