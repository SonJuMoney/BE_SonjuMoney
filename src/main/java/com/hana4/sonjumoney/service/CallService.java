package com.hana4.sonjumoney.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.dto.response.CallRecommendationResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallService {

	private final UserRepository userRepository;

	public List<CallRecommendationResponse> getRecommendations(Long userId, Long targetId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		User target = userRepository.findById(targetId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		String userGender = user.getGender().getValue();
		String targetGender = target.getGender().getValue();
		String userBirth = user.getResidentNum().substring(0, 4);
		String targetBirth = target.getResidentNum().substring(0, 4);
		return null;
	}
}
