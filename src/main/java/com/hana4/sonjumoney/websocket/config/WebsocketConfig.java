package com.hana4.sonjumoney.websocket.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import com.hana4.sonjumoney.websocket.handler.AlarmHandler;
import com.hana4.sonjumoney.websocket.handler.SignalHandler;
import com.hana4.sonjumoney.websocket.interceptor.WebsocketInterceptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {
	private final AlarmHandler alarmHandler;
	private final SignalHandler signalHandler;
	private final WebsocketInterceptor websocketInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(alarmHandler, "/ws/alarms")
			.setAllowedOrigins("*")
			.addInterceptors(websocketInterceptor);

		registry.addHandler(signalHandler, "/ws/signal")
			.setAllowedOrigins("*");
	}

	// @Bean
	// public ServletServerContainerFactoryBean webSocketContainer() {
	// 	ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
	// 	container.setMaxTextMessageBufferSize(8192);
	// 	container.setMaxBinaryMessageBufferSize(8192);
	// 	return container;
	// }
}
