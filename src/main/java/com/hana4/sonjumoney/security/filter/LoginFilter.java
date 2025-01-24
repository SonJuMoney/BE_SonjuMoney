package com.hana4.sonjumoney.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.dto.JwtTokenDto;
import com.hana4.sonjumoney.dto.request.SignInRequest;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.exception.UserNotFoundException;
import com.hana4.sonjumoney.security.model.CustomUserDetails;
import com.hana4.sonjumoney.security.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/api/auth/sign-in");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			SignInRequest signInRequest = objectMapper.readValue(request.getInputStream(), SignInRequest.class);

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				signInRequest.authId(),
				signInRequest.password()
			);

			return authenticationManager.authenticate(authenticationToken);
		} catch (IOException e) {
			throw new AuthenticationServiceException(ErrorCode.BAD_REQUEST.getMessage());
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {

		CustomUserDetails userDetails = (CustomUserDetails)authResult.getPrincipal();

		String authId = userDetails.getAuthId();
		Long userId = userDetails.getUserId();

		String accessToken = jwtUtil.generateAccessToken(authId, userId);
		String refreshToken = jwtUtil.generateRefreshToken(authId, userId);

		JwtTokenDto tokens = JwtTokenDto.of(accessToken, refreshToken, userDetails.getUserId(), userDetails.getName(),
			userDetails.getUserProfile(), userDetails.getGender(), userDetails.getBirthday());

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");

		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(tokens));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		response.setContentType("application/json;charset=UTF-8");
		Throwable cause = failed.getCause();
		if (cause instanceof UserNotFoundException) {
			response.setStatus(ErrorCode.NOT_FOUND_USER.getHttpStatus().value());
			response.getWriter().write(ErrorCode.NOT_FOUND_USER.getMessage());

		} else if (failed instanceof BadCredentialsException) {
			response.setStatus(ErrorCode.INVALID_PASSWORD.getHttpStatus().value());
			response.getWriter().write(ErrorCode.INVALID_PASSWORD.getMessage());

		} else if (failed instanceof AuthenticationServiceException &&
			failed.getMessage().equals(ErrorCode.BAD_REQUEST.getMessage())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write(ErrorCode.BAD_REQUEST.getMessage());

		} else {
			response.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value());
			response.getWriter().write(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
		}
	}
}
