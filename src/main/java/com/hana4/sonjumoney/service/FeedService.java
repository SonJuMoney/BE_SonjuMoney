package com.hana4.sonjumoney.service;

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
import com.hana4.sonjumoney.domain.enums.AlarmType;
import com.hana4.sonjumoney.domain.enums.ContentType;
import com.hana4.sonjumoney.domain.enums.FeedType;
import com.hana4.sonjumoney.dto.ContentPrefix;
import com.hana4.sonjumoney.dto.CreateAlarmDto;
import com.hana4.sonjumoney.dto.CreateAllowanceThanksDto;
import com.hana4.sonjumoney.dto.FeedContentCommentDto;
import com.hana4.sonjumoney.dto.FeedContentContentDto;
import com.hana4.sonjumoney.dto.FeedContentDto;
import com.hana4.sonjumoney.dto.FeedResultDto;
import com.hana4.sonjumoney.dto.request.CreateFeedRequest;
import com.hana4.sonjumoney.dto.request.PostFeedCommentRequest;
import com.hana4.sonjumoney.dto.response.CreateFeedResponse;
import com.hana4.sonjumoney.dto.response.DeleteFeedCommentResponse;
import com.hana4.sonjumoney.dto.response.DeleteFeedResponse;
import com.hana4.sonjumoney.dto.response.FeedLikeResponse;
import com.hana4.sonjumoney.dto.response.FeedResponse;
import com.hana4.sonjumoney.dto.response.PostFeedCommentResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.CommentRepository;
import com.hana4.sonjumoney.repository.FeedContentRepository;
import com.hana4.sonjumoney.repository.FeedRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.util.ContentUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
	private final MemberRepository memberRepository;
	private final FeedRepository feedRepository;
	private final FeedContentRepository feedContentRepository;
	private final S3Service s3Service;
	private final VideoService videoService;
	private final AlarmService alarmService;
	private final CommentRepository commentRepository;
	private static final int PAGE_SIZE = 30;

	@Transactional
	public CreateFeedResponse saveFamilyFeed(Long userId, MultipartFile[] files, CreateFeedRequest createFeedRequest) {
		Member writer = memberRepository.findByUserIdAndFamilyId(userId, createFeedRequest.familyId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		String feedMessage = createFeedRequest.message();
		boolean contentExist = files != null && files.length != 0;
		log.info("contents: " + contentExist);

		Feed savedFeed = feedRepository.save(
			new Feed(writer, null, null, contentExist, 0, feedMessage, FeedType.NORMAL));

		if (contentExist) {
			log.info("컨텐츠 업로드 진입");
			List<MultipartFile> images = new ArrayList<>();
			List<MultipartFile> videos = new ArrayList<>();
			for (MultipartFile file : files) {
				if (file.isEmpty()) {
					continue;
				}
				String contentType = file.getContentType();
				log.info("컨텐츠: " + contentType);
				if (contentType == null) {
					throw new CommonException(ErrorCode.WRONG_FILE_TYPE);
				} else if (contentType.startsWith("image/")) {
					images.add(file);
				} else if (contentType.startsWith("video/")) {
					videos.add(file);
				} else {
					throw new CommonException(ErrorCode.WRONG_FILE_TYPE);
				}
			}
			List<String> contentsUrl = s3Service.uploadImagesToS3(images, ContentPrefix.FEED, savedFeed.getId());
			contentsUrl.addAll(videoService.uploadVideos(videos, ContentPrefix.FEED, savedFeed.getId()));
			for (String contentUrl : contentsUrl) {
				log.info("컨텐츠 url: " + contentUrl);
				feedContentRepository.save(
					new FeedContent(savedFeed, contentUrl));
			}
		}
		alarmService.createOneOffAlarm(
			CreateAlarmDto.of(writer.getFamily().getId(), writer.getId(), savedFeed.getId(), writer.getFamily().getId(),
				AlarmType.FEED));
		return CreateFeedResponse.of(200, savedFeed.getId(), "피드 등록이 완료되었습니다.");
	}

	@Transactional
	public Long saveDirectFeed(CreateAllowanceThanksDto createAllowanceThanksDto) {
		Allowance allowance = createAllowanceThanksDto.allowance();
		FeedType feedType = createAllowanceThanksDto.feedType();
		Member sender;
		Member receiver;
		if (feedType.equals(FeedType.ALLOWANCE)) {
			sender = allowance.getSender();
			receiver = allowance.getReceiver();
		} else if (feedType.equals(FeedType.THANKS)) {
			sender = allowance.getReceiver();
			receiver = allowance.getSender();
		} else {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		MultipartFile file = createAllowanceThanksDto.file();
		boolean contentExist = file != null;
		String message = createAllowanceThanksDto.message();
		Feed savedFeed = feedRepository.save(
			new Feed(sender, allowance, receiver.getId(), contentExist, 0, message,
				createAllowanceThanksDto.feedType()));

		if (contentExist) {
			String contentUrl = uploadOneContent(file, ContentUtil.convertFeedTypeToContentPrefix(feedType),
				savedFeed.getId());
			feedContentRepository.save(new FeedContent(savedFeed, contentUrl));
		}
		return savedFeed.getId();
	}

	private String uploadOneContent(MultipartFile file, ContentPrefix prefix, Long feedId) {
		String contentUrl;
		String contentType = file.getContentType();

		if (contentType == null) {
			throw new CommonException(ErrorCode.WRONG_FILE_TYPE);
		} else if (contentType.startsWith("image/")) {
			contentUrl = s3Service.uploadImageToS3(file, prefix, feedId);
		} else if (contentType.startsWith("video/")) {
			contentUrl = videoService.uploadVideo(file, prefix, feedId);
		} else {
			throw new CommonException(ErrorCode.WRONG_FILE_TYPE);
		}
		return contentUrl;
	}

	@Transactional
	public DeleteFeedResponse deleteFeedById(Long userId, Long feedId) {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		if (!userId.equals(feed.getMember().getUser().getId())) {
			throw new CommonException(ErrorCode.UNAUTHORIZED);
		}
		List<FeedContent> feedContents = feedContentRepository.findAllByFeed(feed);
		for (FeedContent feedContent : feedContents) {
			String pathStr = feedContent.getContentUrl();
			String extension = ContentUtil.getExtension(pathStr);
			if (ContentUtil.classifyContentType(extension).equals(ContentType.IMAGE)) {
				s3Service.deleteImage(pathStr);
			} else {
				videoService.deleteVideo(pathStr);
			}
		}
		feedContentRepository.deleteFeedContentsByFeedId(feedId);
		commentRepository.deleteAllByFeed(feed);
		feedRepository.delete(feed);
		return DeleteFeedResponse.of(200, "삭제가 완료되었습니다.");
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
			Boolean hasNext = feedRepository.hasNext(familyId, feeds.get(feeds.size() - 1).getId());
			List<FeedContentDto> contents = new ArrayList<>();

			for (Feed feed : feeds) {
				if (feed.getFeedType() != FeedType.NORMAL && !feed.getReceiverId().equals(member.getId())) {
					continue;
				}
				List<FeedContent> feedContents = feedContentRepository.findAllByFeed(feed);
				List<Comment> comments = commentRepository.findAllByFeed(feed);
				List<FeedContentContentDto> feedContentContentDtoList = new ArrayList<>();
				List<FeedContentCommentDto> feedContentCommentDtoList = new ArrayList<>();

				for (FeedContent feedContent : feedContents) {
					feedContentContentDtoList.add(FeedContentContentDto.from(feedContent.getContentUrl()));
				}
				for (Comment comment : comments) {
					feedContentCommentDtoList.add(FeedContentCommentDto.from(comment, userId));
				}
				contents.add(FeedContentDto.from(feed, userId, feedContentContentDtoList, feedContentCommentDtoList));
			}
			FeedResultDto result = FeedResultDto.of(hasNext, page, contents);

			return FeedResponse.of(true, 200, "요청성공", result);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public FeedLikeResponse postFeedLike(Long userId, Long feedId) {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Long familyId = feed.getMember().getFamily().getId();
		if (!memberRepository.existsByUserIdAndFamilyId(userId, familyId)) {
			throw new CommonException(ErrorCode.FORBIDDEN);
		}
		try {
			feed.plusLike();
			feedRepository.save(feed);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return FeedLikeResponse.of(200, "요청 성공");

	}

	public PostFeedCommentResponse postFeedComment(Long userId, Long feedId,
		PostFeedCommentRequest postFeedCommentRequest) {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Long familyId = feed.getMember().getFamily().getId();
		try {
			Member member = memberRepository.findByUserIdAndFamilyId(userId, familyId)
				.orElseThrow(() -> new CommonException(ErrorCode.FORBIDDEN));
			Comment comment = Comment.builder()
				.feed(feed)
				.member(member)
				.message(postFeedCommentRequest.message())
				.build();
			commentRepository.save(comment);
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return PostFeedCommentResponse.builder()
			.code(200)
			.message("요청 성공")
			.build();
	}

	public DeleteFeedCommentResponse deleteFeedComment(Long userId, Long commentId) {
		try {
			Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
			Long familyId = comment.getMember().getFamily().getId();
			Member member = memberRepository.findByUserIdAndFamilyId(userId, familyId)
				.orElseThrow(() -> new CommonException(ErrorCode.FORBIDDEN));
			if (!comment.getMember().equals(member)) {
				throw new CommonException(ErrorCode.FORBIDDEN);
			}
			commentRepository.delete(comment);
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return DeleteFeedCommentResponse.builder()
			.code(200)
			.message("요청 성공")
			.build();
	}
}
