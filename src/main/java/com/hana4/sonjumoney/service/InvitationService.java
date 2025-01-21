package com.hana4.sonjumoney.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Invitation;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.enums.InvitationStatus;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.InvitationRepository;
import com.hana4.sonjumoney.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationService {
	private final InvitationRepository invitationRepository;

	private final MemberRepository memberRepository;

	@Transactional
	public Long acceptInvitation(Long userId, Long invitationId) {
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
		return invitation.getFamily().getId();
	}
}
