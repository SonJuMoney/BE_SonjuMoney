package com.hana4.sonjumoney.websocket.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.security.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
			System.out.println("인터셉터 진입");
			if (request instanceof ServletServerHttpRequest) {
				log.info("쿠키 진입");
				HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();
				Cookie[] cookies = servletRequest.getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if ("Authorization".equals(cookie.getName())) {
							String token = cookie.getValue();
							log.info(token);
							Long userId = jwtUtil.getUserId(token);
							attributes.put("userId", userId);
						}
					}
				}
			}
			// HttpHeaders httpHeaders = request.getHeaders();
			// String token = httpHeaders.get("Sec-Websocket-Protocol").get(0);
			// log.info(token);
			// Long userId = jwtUtil.getUserId(token);
			// attributes.put("userId", userId);
			// // TODO 여기서 jwt 인증할 방법 찾기
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
