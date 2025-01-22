package com.hana4.sonjumoney.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Allowance;
import com.hana4.sonjumoney.domain.Comment;
import com.hana4.sonjumoney.domain.Feed;
import com.hana4.sonjumoney.domain.FeedContent;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.enums.ContentType;
import com.hana4.sonjumoney.domain.enums.FeedType;
import com.hana4.sonjumoney.dto.CreateAllowanceThanksDto;
import com.hana4.sonjumoney.dto.FeedContentCommentDto;
import com.hana4.sonjumoney.dto.FeedContentContentDto;
import com.hana4.sonjumoney.dto.FeedContentDto;
import com.hana4.sonjumoney.dto.FeedResultDto;
import com.hana4.sonjumoney.dto.CreateAllowanceThanksDto;
import com.hana4.sonjumoney.dto.ContentPrefix;
import com.hana4.sonjumoney.dto.request.CreateFeedRequest;
import com.hana4.sonjumoney.dto.response.CreateFeedResponse;
import com.hana4.sonjumoney.dto.response.FeedResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.CommentRepository;
import com.hana4.sonjumoney.repository.FeedContentRepository;
import com.hana4.sonjumoney.repository.FeedRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.util.ContentUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {
	private final MemberRepository memberRepository;
	private final FeedRepository feedRepository;
	private final FeedContentRepository feedContentRepository;
	private final S3Service s3Service;
	private final VideoService videoService;
	private static final int PAGE_SIZE = 30;
	private final CommentRepository commentRepository;

	@Transactional
	public CreateFeedResponse saveNormalFeed(Long userId, MultipartFile[] files, CreateFeedRequest createFeedRequest) {
		Member writer = memberRepository.findByUserIdAndFamilyId(userId, createFeedRequest.familyId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		String feedMessage = createFeedRequest.message();
		boolean contentExist;
		if (files == null || files.length == 0) {
			contentExist = false;
		} else {
			contentExist = true;
		}

		Feed savedFeed = feedRepository.save(
			new Feed(writer, null, null, contentExist, 0, feedMessage, FeedType.NORMAL));

		if (contentExist) {
			List<MultipartFile> images = new ArrayList<>();
			List<MultipartFile> videos = new ArrayList<>();
			for (MultipartFile file : files) {
				if (file.isEmpty()) {
					continue;
				}
				String contentType = file.getContentType();
				if (contentType != null) {
					if (contentType.startsWith("image/")) {
						images.add(file);
					}else if (contentType.startsWith("video/")) {
						videos.add(file);
					}
				}
			}
			List<String> contentsUrl = s3Service.uploadImagesToS3(images, ContentPrefix.FEED, savedFeed.getId());
			contentsUrl.addAll(videoService.uploadVideos(videos, ContentPrefix.FEED, savedFeed.getId()));
			for (String contentUrl : contentsUrl) {
				feedContentRepository.save(
					new FeedContent(savedFeed, contentUrl));
			}
		}
		// TODO: 웹소켓 알림 전송
		// alarmService.createOneOffAlarm(CreateAlarmDto.of(writer.getFamily().getId(), writer.getId(), savedFeed.getId(),
		// 	AlarmType.FEED));
		return CreateFeedResponse.of(200, savedFeed.getId(), "피드 등록이 완료되었습니다.");
	}

	@Transactional
	public Long saveAllowanceFeed(CreateAllowanceThanksDto createAllowanceThanksDto) {
		Allowance allowance = createAllowanceThanksDto.allowance();
		Member sender = allowance.getSender();
		Member receiver = allowance.getReceiver();
		boolean contentExist = createAllowanceThanksDto.image() != null;
		String message = createAllowanceThanksDto.message();

		Feed savedFeed = feedRepository.save(
			new Feed(sender, allowance, receiver.getId(), contentExist, 0, message, FeedType.ALLOWANCE));

		if (contentExist) {
			String contentUrl = s3Service.uploadImageToS3(createAllowanceThanksDto.image(), ContentPrefix.ALLOWANCE,
				savedFeed.getId());

			feedContentRepository.save(new FeedContent(savedFeed, contentUrl));
		}
		return savedFeed.getId();
	}

	@Transactional
	public Long saveThanksFeed(CreateAllowanceThanksDto createAllowanceThanksDto) {
		Allowance allowance = createAllowanceThanksDto.allowance();
		Member sender = allowance.getReceiver();
		Member receiver = allowance.getSender();
		boolean contentExist = createAllowanceThanksDto.image() != null;
		String message = createAllowanceThanksDto.message();

		Feed savedFeed = feedRepository.save(
			new Feed(sender, allowance, receiver.getId(), contentExist, 0, message, FeedType.THANKS));

		if (contentExist) {
			String contentUrl = s3Service.uploadImageToS3(createAllowanceThanksDto.image(), ContentPrefix.THANKS,
				savedFeed.getId());

			feedContentRepository.save(new FeedContent(savedFeed, contentUrl));
		}
		return savedFeed.getId();
	}

	@Transactional
	public void deleteFeedById(Long userId, Long feedId) {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		if (!userId.equals(feed.getMember().getUser().getId())) {
			throw new CommonException(ErrorCode.UNAUTHORIZED);
		}
		List<FeedContent> feedContents = feedContentRepository.findAllByFeed(feed);
		for (FeedContent feedContent : feedContents) {
			s3Service.deleteImage(feedContent.getContentUrl());
		}
		feedContentRepository.deleteFeedContentsByFeedId(feedId);
	}

	public FeedResponse getFeeds(Long userId, Long familyId, Integer page) {
		Member member = memberRepository.findByUserIdAndFamilyId(userId, familyId)
			.orElseThrow(() -> new CommonException(ErrorCode.FORBIDDEN));
		try {
			PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
			List<Feed> feeds = feedRepository.findFeedsByFamilyId(familyId, pageRequest);
			if (feeds.isEmpty()) {
				return FeedResponse.of(true, 200, "요청성공", FeedResultDto.of(false, page, new ArrayList<>()));
			}

			// FeedResultDto args start //
			Boolean hasNext = feedRepository.hasNext(familyId, feeds.get(feeds.size() - 1).getId());
			List<FeedContentDto> contents = new ArrayList<>();
			// FeedResultDto args end //

			for (Feed feed : feeds) {
				List<FeedContent> feedContents = feedContentRepository.findAllByFeed(feed);
				List<Comment> comments = commentRepository.findAllByFeed(feed);

				// FeedContentDto args start //
				Long feedId = feed.getId();
				Long writerId = feed.getMember().getUser().getId();
				String writerName = feed.getMember().getUser().getUsername();
				Boolean isMine = userId.equals(writerId);
				String writerImage = feed.getMember().getUser().getProfileLink();
				FeedType feedType = feed.getFeedType();
				String message = feed.getFeedMessage();
				Integer like = feed.getLikes();
				Boolean isUpdate = !feed.getCreatedAt().equals(feed.getUpdatedAt());
				LocalDateTime createdAt = feed.getCreatedAt();
				List<FeedContentContentDto> feedContentContentDtos = new ArrayList<>();
				List<FeedContentCommentDto> feedContentCommentDtos = new ArrayList<>();
				// FeedContentDto args end //

				for (FeedContent feedContent : feedContents) {
					String extension = ContentUtil.getExtension(feedContent.getContentUrl());
					ContentType contentType = ContentUtil.classifyContentType(extension);
					feedContentContentDtos.add(FeedContentContentDto.of(
						feedContent.getContentUrl(),
						contentType
					));
				}

				for (Comment comment : comments) {
					feedContentCommentDtos.add(FeedContentCommentDto.of(
						comment.getId(),
						comment.getMember().getUser().getId(),
						comment.getMember().getUser().getProfileLink(),
						comment.getMessage(),
						!comment.getCreatedAt().equals(comment.getUpdatedAt()),
						comment.getCreatedAt()
					));
				}

				contents.add(FeedContentDto.of(
					feedId,
					writerId,
					writerName,
					isMine,
					writerImage,
					feedType,
					message,
					like,
					isUpdate,
					createdAt,
					feedContentContentDtos,
					feedContentCommentDtos
				));
			}

			FeedResultDto result = FeedResultDto.of(hasNext, page, contents);

			return FeedResponse.of(true, 200, "요청성공", result);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
