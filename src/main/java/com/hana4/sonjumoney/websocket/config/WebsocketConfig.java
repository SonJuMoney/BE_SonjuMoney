package com.hana4.sonjumoney.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hana4.sonjumoney.websocket.handler.AlarmHandler;
import com.hana4.sonjumoney.websocket.interceptor.WebsocketInterceptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {
	private final AlarmHandler alarmHandler;
	private final WebsocketInterceptor websocketInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(alarmHandler, "/ws/alarms")
			.setAllowedOrigins("*")
			.addInterceptors(websocketInterceptor);
	}
}
