package com.hana4.sonjumoney.service;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.dto.response.UserInfoResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public UserInfoResponse getUserByUserId(Long userId) {
		System.out.println("userinfo 서비스 진입");
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		return UserInfoResponse.of(user.getUsername(), user.getProfileLink(), user.getGender(),
			user.getResidentNum().substring(0, 6));
	}
}
