package com.hana4.sonjumoney.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FamilyService {
	private final FamilyRepository familyRepository;
	private final MemberRepository memberRepository;
	private final UserRepository userRepository;

	@Transactional
	public Long createFamily(Long userId, CreateFamilyRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		Family family = familyRepository.save(new Family(
			request.familyName()
		));
		memberRepository.save(new Member(family, user, MemberRole.fromValue(request.role())));

		// TODO: 초대 보내기 구현
		return family.getId();
	}
}
