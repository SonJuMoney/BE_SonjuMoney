package com.hana4.sonjumoney.service;

import java.util.Collections;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.Gender;
import com.hana4.sonjumoney.dto.request.AuthPinRequest;
import com.hana4.sonjumoney.dto.request.SignUpRequest;
import com.hana4.sonjumoney.dto.response.DuplicationResponse;
import com.hana4.sonjumoney.dto.response.PinValidResponse;
import com.hana4.sonjumoney.dto.response.ReissueResponse;
import com.hana4.sonjumoney.dto.response.SignUpResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.exception.UserNotFoundException;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.security.model.CustomUserDetails;
import com.hana4.sonjumoney.security.util.JwtUtil;
import com.hana4.sonjumoney.util.CommonUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

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

	public SignUpResponse signUp(SignUpRequest signUpRequest) {
		//----------예외처리 시작----------//

		Boolean isDuplication = getDuplication(signUpRequest.authId()).duplication();
		if (isDuplication) {
			throw new CommonException(ErrorCode.CONFLICT_USER);
		}
		if (signUpRequest.phone().length() != 11) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		if (signUpRequest.pin().length() != 6) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		if (signUpRequest.residentNum().length() != 13) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}

		//----------예외처리 완료----------//

		try {
			// 주민등록번호 '-' 추가
			String residentNum = signUpRequest.residentNum()
				.substring(0, 6)
				.concat("-")
				.concat(signUpRequest.residentNum().substring(6, 13));
			String password = passwordEncoder.encode(signUpRequest.password());
			Gender gender = CommonUtil.getGender(residentNum);

			User user = User.builder()
				.username(signUpRequest.name())
				.authId(signUpRequest.authId())
				.password(password)
				.phone(signUpRequest.phone())
				.residentNum(residentNum)
				.pin(signUpRequest.pin())
				.gender(gender)
				.build();

			userRepository.save(user);

		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return SignUpResponse.builder()
			.code(201)
			.message("회원가입에 성공했습니다.")
			.build();
	}

	public PinValidResponse validatePin(AuthPinRequest authPinRequest, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));
		String requestPin = authPinRequest.pin();
		String userPin = user.getPin();
		if (!requestPin.equals(userPin)) {
			throw new CommonException(ErrorCode.INVALID_PIN);
		}
		return PinValidResponse.builder()
			.code(200)
			.message("요청을 성공했습니다.")
			.build();
	}
}
