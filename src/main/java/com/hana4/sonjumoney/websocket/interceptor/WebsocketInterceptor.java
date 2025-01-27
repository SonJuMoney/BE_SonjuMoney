package com.hana4.sonjumoney.websocket.interceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.security.util.JwtUtil;
import com.hana4.sonjumoney.util.AuthenticationUtil;

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
		ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest)request;
		Long userId = Long.valueOf(servletServerHttpRequest.getServletRequest().getParameter("userId"));
		log.info("str" + userId);
		attributes.put("userId", userId);
		// log.info("length=" + request.getHeaders().get("cookie").size());
		// String cookie = String.valueOf(request.getHeaders().get("cookie"));
		// System.out.println(cookie);
		// String[] cookies = cookie.split(";");
		// cookies = Arrays.stream(cookies).map(String::trim).toArray(String[]::new);
		// for (String c : cookies) {
		// 	log.info(c);
		// 	if (c.startsWith("authjs.session-token=")) {
		// 		String sessionToken = c.split("=")[1];
		// 		Long userId = jwtUtil.getUserId(sessionToken);
		// 		attributes.put("userId", userId);
		// 	}
		// }
		return true;
		// try {
		// 		HttpHeaders httpHeaders = request.getHeaders();
		// 	String token = httpHeaders.get("Sec-Websocket-Protocol").get(0);
		// 	log.info(token);
		// 	Long userId = jwtUtil.getUserId(token);
		// 	attributes.put("userId", userId);
		// 	// TODO 여기서 jwt 인증할 방법 찾기
		// 	return true;
		// } catch (Exception e) {
		// 	throw new CommonException(ErrorCode.UNAUTHORIZED);
		// }
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {

	}
}
