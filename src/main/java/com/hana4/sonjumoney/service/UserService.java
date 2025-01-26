package com.hana4.sonjumoney.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Relationship;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.dto.ContentPrefix;
import com.hana4.sonjumoney.dto.response.GetChildrenResponse;
import com.hana4.sonjumoney.dto.response.UpdateProfileResponse;
import com.hana4.sonjumoney.dto.response.UserInfoResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.RelationshipRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	private final RelationshipRepository relationshipRepository;
	private final S3Service s3Service;

	public UpdateProfileResponse updateProfile(Long userId, MultipartFile file) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

		if (file == null || file.isEmpty() || !Objects.requireNonNull(file.getContentType())
			.startsWith("image/")) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		try {
			if (user.getProfileLink() != null) {
				s3Service.deleteImage(user.getProfileLink());
				user.setProfileLink(null);
			}
			String contentsUrl = s3Service.uploadImageToS3(file, ContentPrefix.PROFILE, userId);
			user.setProfileLink(contentsUrl);
			userRepository.save(user);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return UpdateProfileResponse.builder()
			.code(200)
			.message("이미지 변경이 완료되었습니다.")
			.url(user.getProfileLink())
			.build();
	}

	public UserInfoResponse getUserByUserId(Long userId) {
		System.out.println("userinfo 서비스 진입");
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		return UserInfoResponse.of(user.getUsername(), user.getProfileLink(), user.getGender(),
			user.getResidentNum().substring(0, 6));
	}

	public List<GetChildrenResponse> getChildren(Long userId) {
		List<Relationship> children = relationshipRepository.findAllByParent_Id(userId);
		if (children.isEmpty()) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}
		return children.stream().map(relationship ->
			GetChildrenResponse.of(
				relationship.getChild().getId(),
				relationship.getChild().getUsername()
			)).toList();
	}
}
