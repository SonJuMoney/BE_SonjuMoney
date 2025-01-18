package com.hana4.sonjumoney.service;

import com.hana4.sonjumoney.domain.enums.AlarmType;
import com.hana4.sonjumoney.dto.response.SendAllowanceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Allowance;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.dto.AllowanceDto;
import com.hana4.sonjumoney.dto.CreateAllowanceDto;
import com.hana4.sonjumoney.dto.request.SendAllowanceRequest;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AllowanceRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.websocket.dto.AlarmDto;
import com.hana4.sonjumoney.websocket.handler.WebsocketHandler;

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
	private final WebsocketHandler websocketHandler;

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

		Allowance allowance = allowanceRepository.save(
			new Allowance(sender,receiver,sendAllowanceRequest.amount())
		);
		Long feedId = feedService.saveAllowanceFeed(
			CreateAllowanceDto.of(allowance, image, sendAllowanceRequest.message()));
		// TODO: 웹소켓 테스트 생각해오기
		// websocketHandler.sendMemberAlarm(AlarmDto.of(receiver.getId(), sender.getId(), AlarmType.ALLOWANCE));
		return SendAllowanceResponse.of("송금을 완료했습니다.");
	}
}
