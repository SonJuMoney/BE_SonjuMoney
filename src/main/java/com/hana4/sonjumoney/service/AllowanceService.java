package com.hana4.sonjumoney.service;

import javax.management.relation.Relation;

import com.hana4.sonjumoney.domain.Relationship;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AlarmType;
import com.hana4.sonjumoney.domain.enums.FeedType;
import com.hana4.sonjumoney.dto.CreateAlarmDto;
import com.hana4.sonjumoney.dto.TransactionHistoryDto;
import com.hana4.sonjumoney.dto.request.SendThanksRequest;
import com.hana4.sonjumoney.dto.response.AllowanceInfoResponse;
import com.hana4.sonjumoney.dto.response.SendAllowanceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Allowance;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.dto.AllowanceDto;
import com.hana4.sonjumoney.dto.CreateAllowanceThanksDto;
import com.hana4.sonjumoney.dto.request.SendAllowanceRequest;
import com.hana4.sonjumoney.dto.response.SendThanksResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AllowanceRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.RelationshipRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllowanceService {
	private final AllowanceRepository allowanceRepository;
	private final MemberRepository memberRepository;
	private final RelationshipRepository relationshipRepository;
	private final AccountService accountService;
	private final FeedService feedService;
	private final AlarmService alarmService;

	@Transactional
	public SendAllowanceResponse sendAllowance(MultipartFile file, Long userId, SendAllowanceRequest sendAllowanceRequest) {
		Member receiver = memberRepository.findByIdWithUser(sendAllowanceRequest.receiverId())
			.orElseThrow(() -> new CommonException(
				ErrorCode.NOT_FOUND_MEMBER));
		Member sender = memberRepository.findByUser_IdAndFamily(userId, receiver.getFamily())
			.orElseThrow(() -> new CommonException(
				ErrorCode.NOT_FOUND_MEMBER));
		User receiverUser = receiver.getUser();

		if (!sender.getFamily().equals(receiver.getFamily())) {
			throw new CommonException(ErrorCode.DIFFERENT_FAMILY);
		}

		String message = sendAllowanceRequest.message();
		accountService.makeTransferByUserId(
			AllowanceDto.of(sender.getUser().getId(), receiverUser.getId(), sendAllowanceRequest.amount(),
				message != null ? message : ""));

		Allowance savedAllowance = allowanceRepository.save(
			new Allowance(sender,receiver,sendAllowanceRequest.amount())
		);

		if (message != null && !message.isEmpty()) {
			feedService.saveDirectFeed(
				CreateAllowanceThanksDto.of(savedAllowance, file, message, FeedType.ALLOWANCE));
		}
		if (receiverUser.getPhone() == null) {
			Relationship relationship = relationshipRepository.findByChildId(receiverUser.getId());
			receiverUser = relationship.getParent();
		}
		alarmService.createOneOffAlarm(
			CreateAlarmDto.of(receiverUser.getId(), sender.getId(), savedAllowance.getId(), receiver.getFamily().getId(),
				AlarmType.ALLOWANCE));
		return SendAllowanceResponse.of(200, "송금을 완료했습니다.", savedAllowance.getId());
	}

	public SendThanksResponse sendThanks(MultipartFile file, Long userId, Long allowanceId, SendThanksRequest sendThanksRequest) {
		Allowance allowance = allowanceRepository.findById(allowanceId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Member receiver = allowance.getSender();
		Member sender = allowance.getReceiver();
		if (!memberRepository.findByUser_IdAndFamily(userId, receiver.getFamily())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER))
			.equals(sender)) {
			throw new CommonException(ErrorCode.DIFFERENT_MEMBER_USER);
		}
		String message = sendThanksRequest.message();
		if (message != null) {
			Long feedId = feedService.saveDirectFeed(
				CreateAllowanceThanksDto.of(allowance, file, message, FeedType.THANKS));
			alarmService.createOneOffAlarm(
				CreateAlarmDto.of(receiver.getUser().getId(), sender.getId(), feedId, receiver.getFamily().getId(),
					AlarmType.THANKS));
		} else {
			throw new CommonException(ErrorCode.NULL_THANKS_MESSAGE);
		}
		return SendThanksResponse.of(200, "감사 메시지를 전송했습니다.");
	}

	public AllowanceInfoResponse getAllowanceById(Long allowanceId) {
		return AllowanceInfoResponse.from(
			allowanceRepository.findById(allowanceId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA)));
	}
}
