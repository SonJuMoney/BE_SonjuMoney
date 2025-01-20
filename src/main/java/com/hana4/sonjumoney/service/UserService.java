package com.hana4.sonjumoney.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.Relationship;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.dto.response.GetChildrenResponse;
import com.hana4.sonjumoney.dto.response.UserInfoResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.RelationshipRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	private final RelationshipRepository relationshipRepository;

	public UserInfoResponse getUserByUserId(Long userId) {
		System.out.println("userinfo 서비스 진입");
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		return UserInfoResponse.of(user.getUsername(), user.getProfileLink(), user.getGender(),
			user.getResidentNum().substring(0, 6));
	}

	public List<GetChildrenResponse> getChildren(Long userId) {
		List<Relationship> children = relationshipRepository.findAllByParent_Id(userId);
		if (children.isEmpty()) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}
		return children.stream().map(relationship ->
			GetChildrenResponse.of(
				relationship.getChild().getId(),
				relationship.getChild().getUsername()
			)).toList();
	}
}
