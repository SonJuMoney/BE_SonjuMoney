package com.hana4.sonjumoney.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.GetFamilyResponse;
import com.hana4.sonjumoney.dto.response.MemberResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FamilyService {
	private final MemberRepository memberRepository;
	private final FamilyRepository familyRepository;
	private final UserRepository userRepository;

	public List<GetFamilyResponse> findFamilies(Long userId) {
		// 유저가 속한 가족(들)의 family_id를 찾는다.
		List<Member> member = memberRepository.findAllByUserId(userId);
		List<GetFamilyResponse> responses = new ArrayList<>();

		for (Member m : member) {
			Family family = familyRepository.findById(m.getFamily().getId())
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

			List<MemberResponse> memberResponses = new ArrayList<>();
			List<Member> findByFamilyIdMembers = memberRepository.findByFamilyId(family.getId());
			for (Member fbfMember : findByFamilyIdMembers) {
				memberResponses.add(
					MemberResponse.of(fbfMember.getId(), fbfMember.getUser().getId(), fbfMember.getUser().getUsername(),
						fbfMember.getMemberRole()));
			}
			GetFamilyResponse getFamilyResponse = GetFamilyResponse.of(family.getId(), family.getFamilyName(),
				memberResponses);
			responses.add(getFamilyResponse);
		}

		return responses;
	}

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
