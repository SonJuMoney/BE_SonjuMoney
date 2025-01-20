package com.hana4.sonjumoney.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AllDayStatus;
import com.hana4.sonjumoney.domain.enums.EventCategory;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.dto.request.AddEventRequest;
import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.GetFamilyResponse;
import com.hana4.sonjumoney.dto.response.MemberResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.util.CommonUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FamilyService {
	private final MemberRepository memberRepository;
	private final FamilyRepository familyRepository;
	private final UserRepository userRepository;
	private final EventService eventService;

	public List<GetFamilyResponse> findFamilies(Long userId) {
		// 유저가 속한 가족(들)의 family_id를 찾는다.
		List<Member> members = memberRepository.findAllByUserId(userId);
		List<GetFamilyResponse> responses = new ArrayList<>();

		for (Member m : members) {
			Family family = familyRepository.findById(m.getFamily().getId())
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

			List<MemberResponse> memberResponses = new ArrayList<>();
			List<Member> findByFamilyIdMembers = memberRepository.findByFamilyId(family.getId());
			for (Member fbfMember : findByFamilyIdMembers) {
				memberResponses.add(
					MemberResponse.of(fbfMember.getId(), fbfMember.getUser().getId(), fbfMember.getUser().getUsername(),
						fbfMember.getMemberRole().getValue()));
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
		Member member = memberRepository.save(new Member(family, user, MemberRole.fromValue(request.role())));
		addBirthWhenMemberAdded(userId, user.getUsername(), user.getResidentNum(), member.getId(), family.getId());
		// TODO: 초대 보내기 구현
		return family.getId();

	}

	private void addBirthWhenMemberAdded(Long userId, String userName, String residentNum, Long memberId,
		Long familyId) {
		LocalDateTime birth = CommonUtil.getBirthThisYear(residentNum);
		int cnt = 0;
		while (cnt < 5) {
			LocalDateTime eventDate = birth.plusYears(cnt);
			eventService.addEvent(userId, familyId,
				AddEventRequest.of(EventCategory.BIRTHDAY, userName + "의 생일", eventDate, eventDate,
					AllDayStatus.ALL_DAY, Collections.singletonList(memberId)));
			cnt += 1;
		}
	}
}
