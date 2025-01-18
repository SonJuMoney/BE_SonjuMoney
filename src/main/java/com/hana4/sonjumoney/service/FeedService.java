package com.hana4.sonjumoney.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Allowance;
import com.hana4.sonjumoney.domain.Feed;
import com.hana4.sonjumoney.domain.FeedContent;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.enums.FeedType;
import com.hana4.sonjumoney.dto.CreateAllowanceDto;
import com.hana4.sonjumoney.dto.ImagePrefix;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FeedContentRepository;
import com.hana4.sonjumoney.repository.FeedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {
	private final FeedRepository feedRepository;
	private final FeedContentRepository feedContentRepository;
	private final S3Service s3Service;

	@Transactional
	public Long saveAllowanceFeed(CreateAllowanceDto createAllowanceDto) {
		Allowance allowance = createAllowanceDto.allowance();
		Member sender = allowance.getSender();
		Member receiver = allowance.getReceiver();
		boolean isExist = createAllowanceDto.image() != null;
		String message = createAllowanceDto.message();

		Feed savedFeed = feedRepository.save(
			new Feed(sender, allowance, receiver.getId(), isExist, 0, message, FeedType.ALLOWANCE));

		if (isExist) {
			MultipartFile image = createAllowanceDto.image();
			String contentUrl;

			try {
				contentUrl = s3Service.uploadImageToS3(image, ImagePrefix.ALLOWANCE);
			} catch (IOException e) {
				throw new CommonException(ErrorCode.IMAGE_UPLOAD_FAILED);
			}

			feedContentRepository.save(
				new FeedContent(savedFeed, contentUrl)
			);
		}
		return savedFeed.getId();
	}
}
