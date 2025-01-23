package com.hana4.sonjumoney.websocket.interceptor;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketInterceptor implements HandshakeInterceptor {
	private final JwtUtil jwtUtil;
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) {
		try {
			HttpHeaders httpHeaders = request.getHeaders();
			String token = httpHeaders.get("Sec-WebSocket-Protocol").get(0);
			// TODO 여기서 jwt 인증할 방법 찾기
			Long userId = jwtUtil.getUserId(token);
			attributes.put("userId", userId);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.UNAUTHORIZED);
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {

	}
}
