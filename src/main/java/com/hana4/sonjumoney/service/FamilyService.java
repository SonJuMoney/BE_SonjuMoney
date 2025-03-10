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
import com.hana4.sonjumoney.domain.enums.Gender;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.dto.InviteChildDto;
import com.hana4.sonjumoney.dto.request.AddEventRequest;
import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.GetFamilyMemberResponse;
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
	private final InvitationService invitationService;

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
						fbfMember.getMemberRole().getValue(), fbfMember.getUser().getProfileLink()));
			}
			GetFamilyResponse getFamilyResponse = GetFamilyResponse.of(family.getId(), family.getFamilyName(),
				memberResponses);
			responses.add(getFamilyResponse);
		}

		return responses;
	}

	public GetFamilyMemberResponse findFamilyMembers(Long userId, Long familyId, String range) {
		List<Member> members;
		try {
			switch (range) {
				case "ALL" -> {
					members = memberRepository.findByFamilyId(familyId);
				}
				case "EXCEPTME" -> {
					members = memberRepository.findFamilyExceptUser(userId, familyId);
				}
				case "CHILDREN" -> {
					members = memberRepository.findChildren(familyId);
				}

				default -> throw new CommonException(ErrorCode.BAD_REQUEST);
			}
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		// 가족이 없는 유저 대상 본인 제외 혹은 자녀만 조회할 때 가족 정보 + 빈 members 전달하도록 처리
		if (members.isEmpty()) {
			Family family = familyRepository.findById(familyId)
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
			return GetFamilyMemberResponse.of(familyId, family.getFamilyName(), members);
		}
		return GetFamilyMemberResponse.of(familyId, members.get(0).getFamily().getFamilyName(), members);
	}

	@Transactional
	public Long createFamily(Long userId, CreateFamilyRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		Family family = familyRepository.save(new Family(
			request.familyName()
		));
		Member member = memberRepository.save(new Member(family, user, MemberRole.fromValue(request.role())));
		List<InviteChildDto> childDtoList = request.addChildren();

		if (childDtoList != null && !childDtoList.isEmpty()) {
			for (InviteChildDto childDto : childDtoList) {
				User child = userRepository.findById(childDto.userId())
					.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
				memberRepository.save(
					new Member(family, child,
						child.getGender().equals(Gender.MALE) ? MemberRole.SON : MemberRole.DAUGHTER));
			}
		}
		addBirthWhenMemberAdded(userId, user.getUsername(), user.getResidentNum(), member.getId(), family.getId());
		invitationService.sendInvitation(family.getId(), member.getId(), request.addMembers());
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
					AllDayStatus.ALL_DAY, Collections.singletonList(memberId)), false);
			cnt += 1;
		}
	}
}
