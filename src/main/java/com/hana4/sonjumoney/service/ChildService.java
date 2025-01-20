package com.hana4.sonjumoney.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.Relationship;
import com.hana4.sonjumoney.dto.response.GetChildrenResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.RelationshipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChildService {
	private final RelationshipRepository relationshipRepository;

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
