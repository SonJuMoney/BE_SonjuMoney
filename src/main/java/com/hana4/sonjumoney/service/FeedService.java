package com.hana4.sonjumoney.service;

import java.util.List;

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
import com.hana4.sonjumoney.dto.request.CreateFeedRequest;
import com.hana4.sonjumoney.dto.response.CreateFeedResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FeedContentRepository;
import com.hana4.sonjumoney.repository.FeedRepository;
import com.hana4.sonjumoney.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {
	private final MemberRepository memberRepository;
	private final FeedRepository feedRepository;
	private final FeedContentRepository feedContentRepository;
	private final S3Service s3Service;

	@Transactional
	public CreateFeedResponse saveNormalFeed(Long userId, MultipartFile[] images, CreateFeedRequest createFeedRequest) {
		Member writer = memberRepository.findByUserIdAndFamilyId(userId, createFeedRequest.familyId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		String feedMessage = createFeedRequest.message();
		boolean contentExist;
		if (images == null || images.length == 0) {
			contentExist = false;
		} else {
			contentExist = true;
		}

		Feed savedFeed = feedRepository.save(
			new Feed(writer, null, null, contentExist, 0, feedMessage, FeedType.NORMAL));

		if (contentExist) {
			List<String> contentsUrl = s3Service.uploadImagesToS3(images, ImagePrefix.FEED, savedFeed.getId());
			for (String contentUrl : contentsUrl) {
				feedContentRepository.save(
					new FeedContent(savedFeed, contentUrl));
			}
		}
		// TODO: 웹소켓 알림 전송
		return CreateFeedResponse.of(200, savedFeed.getId(), "피드 등록이 완료되었습니다.");
	}

	@Transactional
	public Long saveAllowanceFeed(CreateAllowanceDto createAllowanceDto) {
		Allowance allowance = createAllowanceDto.allowance();
		Member sender = allowance.getSender();
		Member receiver = allowance.getReceiver();
		boolean  contentExist = createAllowanceDto.image() != null;
		String message = createAllowanceDto.message();

		Feed savedFeed = feedRepository.save(
			new Feed(sender, allowance, receiver.getId(), contentExist, 0, message, FeedType.ALLOWANCE));

		if (contentExist) {
			MultipartFile image = createAllowanceDto.image();
			String contentUrl = s3Service.uploadImageToS3(image, ImagePrefix.ALLOWANCE, savedFeed.getId());

			feedContentRepository.save(
				new FeedContent(savedFeed, contentUrl)
			);
		}
		return savedFeed.getId();
	}

	@Transactional
	public void deleteFeedById(Long feedId) {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		List<FeedContent> feedContents = feedContentRepository.findAllByFeed(feed);
		for (FeedContent feedContent : feedContents) {
			s3Service.deleteImage(feedContent.getContentUrl());
		}
		feedContentRepository.deleteFeedContentsByFeedId(feedId);
	}
}
