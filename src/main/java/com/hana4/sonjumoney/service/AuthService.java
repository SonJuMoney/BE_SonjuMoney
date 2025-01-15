package com.hana4.sonjumoney.service;

import java.util.Collections;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.dto.response.DuplicationResponse;
import com.hana4.sonjumoney.dto.response.ReissueResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.exception.UserNotFoundException;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.security.model.CustomUserDetails;
import com.hana4.sonjumoney.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	@Override
	public UserDetails loadUserByUsername(String authId) throws AuthenticationException {
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));

		return new CustomUserDetails(user.getId(), user.getAuthId(), user.getPassword(), Collections.emptyList());
	}

	public ReissueResponse reissue(String refreshToken) {
		if (!jwtUtil.validateRefreshToken(refreshToken)) {
			throw new
				CommonException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);

		return new ReissueResponse(newAccessToken);
	}

	public DuplicationResponse getDuplication(String authId) {
		try {
			User user = userRepository.findByAuthId(authId)
				.orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));
		} catch (UserNotFoundException e) {
			return DuplicationResponse.of(false);
		}
		return DuplicationResponse.of(true);
	}
}
