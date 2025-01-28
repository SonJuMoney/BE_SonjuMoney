package com.hana4.sonjumoney.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.Gender;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.dto.response.CallRecommendationResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.util.CommonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallService {

	private final UserRepository userRepository;
	private final ChatGPTService chatGPTService;

	public List<CallRecommendationResponse> getRecommendations(Long userId, Long targetId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		User target = userRepository.findById(targetId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		Gender userGender = user.getGender();
		Gender targetGender = target.getGender();
		Integer userBirth = CommonUtil.traslateBirth(user.getResidentNum());
		Integer targetBirth = CommonUtil.traslateBirth(target.getResidentNum());
		List<MemberRole> memberRoles = CommonUtil.getMemberRoles(userBirth, userGender, targetBirth, targetGender);
		String userRole = CommonUtil.translateRole(memberRoles.get(0));
		String targetRole = CommonUtil.translateRole(memberRoles.get(1));
		StringBuilder builder = new StringBuilder();
		builder
			.append("대한민국에 살고 있는 ")
			.append(userBirth)
			.append("년생 ")
			.append(userGender.getValue())
			.append("성과, 그 대한민국에 살고 있는 ")
			.append(targetBirth)
			.append("년생 ")
			.append(targetGender.getValue())
			.append("성이 영상 통화를 하고 있어. 이 둘은 ")
			.append(userRole)
			.append(", ")
			.append(targetRole)
			.append(" 관계야. 그런데 이 둘의 관심사나 요즘 유행하는 주제를 잘 몰라서 어떤 주제로 대화해야 할지 어려운 상황이야. 이 때 ")
			.append(userRole)
			.append("가 ")
			.append(targetRole)
			.append("에게 이야기 할 수 있는 주제 5개를 알려줘. ")
			.append(userRole)
			.append("가 ")
			.append(targetRole)
			.append("에게 바로 물어볼 수 있도록 대화체의 의문문 형태로 알려줘.");

		String prompt = builder.toString();
		String[] response = chatGPTService.askChatGPT(prompt).split("\\n");
		List<CallRecommendationResponse> recommendations = new ArrayList<CallRecommendationResponse>();
		for (String s : response) {
			recommendations.add(CallRecommendationResponse.of(s));
		}

		return recommendations;
	}
}
