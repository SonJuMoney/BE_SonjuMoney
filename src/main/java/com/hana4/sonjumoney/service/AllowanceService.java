package com.hana4.sonjumoney.service;

import com.hana4.sonjumoney.domain.enums.AlarmType;
import com.hana4.sonjumoney.dto.CreateAlarmDto;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllowanceService {
	private final AllowanceRepository allowanceRepository;
	private final MemberRepository memberRepository;
	private final AccountService accountService;
	private final FeedService feedService;
	private final AlarmService alarmService;

	@Transactional
	public SendAllowanceResponse sendAllowance(MultipartFile image, Long userId, SendAllowanceRequest sendAllowanceRequest) {
		Member receiver = memberRepository.findById(sendAllowanceRequest.receiverId())
			.orElseThrow(() -> new CommonException(
				ErrorCode.NOT_FOUND_MEMBER));
		Member sender = memberRepository.findByUser_IdAndFamily(userId, receiver.getFamily())
			.orElseThrow(() -> new CommonException(
				ErrorCode.NOT_FOUND_MEMBER));

		if (!sender.getFamily().equals(receiver.getFamily())) {
			throw new CommonException(ErrorCode.DIFFERENT_FAMILY);
		}

		accountService.makeTransferByUserId(AllowanceDto.of(sender.getUser().getId(), receiver.getUser().getId(),
			sendAllowanceRequest.amount()));

		Allowance savedAllowance = allowanceRepository.save(
			new Allowance(sender,receiver,sendAllowanceRequest.amount())
		);
		Long feedId = feedService.saveAllowanceFeed(
			CreateAllowanceThanksDto.of(savedAllowance, image, sendAllowanceRequest.message()));

		alarmService.createOneOffAlarm(
			CreateAlarmDto.of(receiver.getUser().getId(), sender.getId(), feedId, AlarmType.ALLOWANCE));
		return SendAllowanceResponse.of(200, "송금을 완료했습니다.", savedAllowance.getId());
	}

	public SendThanksResponse sendThanks(MultipartFile image, Long userId, Long allowanceId,SendThanksRequest sendThanksRequest) {
		Allowance allowance = allowanceRepository.findById(allowanceId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Member sender = allowance.getReceiver();
		Member receiver = allowance.getSender();
		String thanksMessage = sendThanksRequest.message();
		Long feedId = feedService.saveThanksFeed(CreateAllowanceThanksDto.of(allowance, image, thanksMessage));
		alarmService.createOneOffAlarm(
			CreateAlarmDto.of(receiver.getUser().getId(), sender.getId(), feedId, AlarmType.THANKS));
		return SendThanksResponse.of(200, "감사 메시지를 전송했습니다.");
	}

	public AllowanceInfoResponse getAllowanceById(Long allowanceId) {
		return AllowanceInfoResponse.from(
			allowanceRepository.findById(allowanceId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA)));
	}
}
