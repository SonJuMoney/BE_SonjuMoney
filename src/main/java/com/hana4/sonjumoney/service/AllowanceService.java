package com.hana4.sonjumoney.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Allowance;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.dto.TransactionDto;
import com.hana4.sonjumoney.dto.request.SendAllowanceRequest;
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
	private final S3Service s3Service;

	public String uploadTest(MultipartFile image, SendAllowanceRequest sendAllowanceRequest) {
		log.info("이미지 업로드 테스트 서비스 진입");
		s3Service.upload(image);
		return "test";
	}
	public String sendAllowance(MultipartFile image, SendAllowanceRequest sendAllowanceRequest) {

		Member sender = memberRepository.findById(sendAllowanceRequest.recieverId())
			.orElseThrow(() -> new CommonException(
				ErrorCode.NOT_FOUND_MEMBER));
		Member receiver = memberRepository.findById(sendAllowanceRequest.recieverId())
			.orElseThrow(() -> new CommonException(
				ErrorCode.NOT_FOUND_MEMBER));
		accountService.makeTransferByUserId(new TransactionDto(sender.getUser().getId(), receiver.getUser().getId(),
			sendAllowanceRequest.amount()));
		s3Service.upload(image);
		allowanceRepository.save(Allowance.builder()
			.receiver(receiver)
			.sender(sender)
			.amount(sendAllowanceRequest.amount())
			.build());

		return "Success";
	}
}
