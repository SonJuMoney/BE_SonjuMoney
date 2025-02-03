package com.hana4.sonjumoney.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Invitation;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AlarmType;
import com.hana4.sonjumoney.domain.enums.InvitationStatus;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.dto.CreateAlarmDto;
import com.hana4.sonjumoney.dto.InviteChildDto;
import com.hana4.sonjumoney.dto.InviteUserDto;
import com.hana4.sonjumoney.dto.request.CreateFamilyRequest;
import com.hana4.sonjumoney.dto.response.AcceptInvitationResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.InvitationRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.RelationshipRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {
	private final InvitationRepository invitationRepository;
	private final MemberRepository memberRepository;
	private final UserRepository userRepository;
	private final AlarmService alarmService;
	private final RelationshipRepository relationshipRepository;
	private final FamilyRepository familyRepository;

	@Transactional
	public AcceptInvitationResponse acceptInvitation(Long userId, Long invitationId) {
		Invitation invitation = invitationRepository.findById(invitationId).orElseThrow(() -> new CommonException(
			ErrorCode.NOT_FOUND_DATA));
		if (!invitation.getInvitee().getId().equals(userId)) {
			throw new CommonException(ErrorCode.UNAUTHORIZED);
		}
		if (invitation.getInvitationStatus() == InvitationStatus.ACCEPTED) {
			throw new CommonException(ErrorCode.CONFLICT_INVITATION);
		}
		invitation.updateInvitationStatus(
			InvitationStatus.ACCEPTED
		);
		invitationRepository.save(invitation);

		Member member = Member.builder()
			.family(invitation.getFamily())
			.user(invitation.getInvitee())
			.memberRole(invitation.getMemberRole())
			.build();
		memberRepository.save(member);
		return AcceptInvitationResponse.of(201, invitation.getFamily().getId());
	}

	public void sendInvitation(Long familyId, Long senderId, List<InviteUserDto> addMembers) {
		Member sender = memberRepository.findById(senderId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
		Family family = familyRepository.findById(familyId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		for (InviteUserDto invitee : addMembers) {
			List<User> users = userRepository.findUsersByPhone(invitee.phone());
			User invitedUser = null;
			if (users.isEmpty()) {
				continue;
			} else {
				for (User user : users) {
					if (relationshipRepository.existsByParentId(user.getId())) {
						invitedUser = user;
						break;
					}
				}
			}
			Invitation savedInvitation = invitationRepository.save(
				new Invitation(sender.getUser(), invitedUser, family, MemberRole.fromValue(invitee.role()),
					InvitationStatus.PENDING));

			if (invitedUser != null) {
				log.info("초대 보낼 유저: " + invitedUser.getId());
				alarmService.createOneOffAlarm(
					CreateAlarmDto.of(invitedUser.getId(), senderId, savedInvitation.getId(), familyId, AlarmType.INVITE));
			}
		}
	}
}
