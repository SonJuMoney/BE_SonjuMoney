package com.hana4.sonjumoney.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.dto.response.GetFamilyResponse;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FamilyService {
	private final MemberRepository memberRepository;
	private final FamilyRepository familyRepository;

	public List<GetFamilyResponse> findFamilies(Long userId) {
		Member member = memberRepository.findByUserId(userId);
		List<Family> families = familyRepository.findAllByFamilyId(member.getFamily().getId());

		return families.stream()
			.map(family -> GetFamilyResponse.of(family.getId(), family.getFamilyName(), family.getCreatedAt(),
				family.getUpdatedAt())).toList();
	}
}
