package com.hana4.sonjumoney.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.Relationship;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.Gender;
import com.hana4.sonjumoney.domain.enums.Role;
import com.hana4.sonjumoney.dto.JwtTokenDto;
import com.hana4.sonjumoney.dto.request.AuthPinRequest;
import com.hana4.sonjumoney.dto.request.SignUpChildRequest;
import com.hana4.sonjumoney.dto.request.SignUpRequest;
import com.hana4.sonjumoney.dto.request.SwitchAccountRequest;
import com.hana4.sonjumoney.dto.response.AuthListResponse;
import com.hana4.sonjumoney.dto.response.DuplicationResponse;
import com.hana4.sonjumoney.dto.response.PinValidResponse;
import com.hana4.sonjumoney.dto.response.ReissueResponse;
import com.hana4.sonjumoney.dto.response.SignUpChildResponse;
import com.hana4.sonjumoney.dto.response.SignUpResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.exception.UserNotFoundException;
import com.hana4.sonjumoney.repository.RelationshipRepository;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.security.model.CustomUserDetails;
import com.hana4.sonjumoney.security.util.JwtUtil;
import com.hana4.sonjumoney.util.CommonUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final RelationshipRepository relationshipRepository;

	@Override
	public UserDetails loadUserByUsername(String authId) throws AuthenticationException {
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));

		// return new CustomUserDetails(user.getId(), user.getAuthId(), user.getPassword(), Collections.emptyList());
		return new CustomUserDetails(user.getId(), user.getAuthId(), user.getPassword(), user.getUsername(),
			user.getProfileLink(), user.getGender(), user.getResidentNum().substring(0, 6), Collections.emptyList());
	}

	public ReissueResponse reissue(String refreshToken) {
		if (!jwtUtil.validateRefreshToken(refreshToken)) {
			throw new
				CommonException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);

		return new ReissueResponse(newAccessToken);
	}

	public DuplicationResponse getDuplication(String authId) {
		try {
			User user = userRepository.findByAuthId(authId)
				.orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));
		} catch (UserNotFoundException e) {
			return DuplicationResponse.of(false);
		}
		return DuplicationResponse.of(true);
	}

	private Boolean isDuplicatedByResidentNum(String residentNumWithoutHyphen) {
		String residentNum = addHyphenFromResidentNum(residentNumWithoutHyphen);
		return userRepository.findByResidentNum(residentNum).isPresent();
	}

	private String addHyphenFromResidentNum(String residentNum) {
		return residentNum
			.substring(0, 6)
			.concat("-")
			.concat(residentNum.substring(6, 13));
	}

	public SignUpResponse signUp(SignUpRequest signUpRequest) {
		//----------예외처리 시작----------//

		Boolean isDuplication = getDuplication(signUpRequest.authId()).duplication();
		if (isDuplication) {
			throw new CommonException(ErrorCode.CONFLICT_ID);
		}
		Boolean isDuplicatedByResidentNum = isDuplicatedByResidentNum(signUpRequest.residentNum());
		if (isDuplicatedByResidentNum) {
			throw new CommonException(ErrorCode.CONFLICT_USER);
		}
		if (signUpRequest.phone().length() != 11) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		if (signUpRequest.pin().length() != 6) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		if (signUpRequest.residentNum().length() != 13) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}

		//----------예외처리 완료----------//

		try {
			// 주민등록번호 '-' 추가
			String residentNum = addHyphenFromResidentNum(signUpRequest.residentNum());
			String password = passwordEncoder.encode(signUpRequest.password());
			Gender gender = CommonUtil.getGender(residentNum);

			User user = User.builder()
				.username(signUpRequest.name())
				.authId(signUpRequest.authId())
				.password(password)
				.phone(signUpRequest.phone())
				.residentNum(residentNum)
				.pin(signUpRequest.pin())
				.gender(gender)
				.build();

			userRepository.save(user);

		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return SignUpResponse.builder()
			.code(201)
			.message("회원가입에 성공했습니다.")
			.build();
	}

	@Transactional
	public SignUpChildResponse signUpChild(SignUpChildRequest signUpChildRequest, Long parentId) {
		//----------예외처리 시작----------//
		Boolean isDuplication = getDuplication(signUpChildRequest.authId()).duplication();
		if (isDuplication) {
			throw new CommonException(ErrorCode.CONFLICT_ID);
		}
		Boolean isDuplicatedByResidentNum = isDuplicatedByResidentNum(signUpChildRequest.residentNum());
		if (isDuplicatedByResidentNum) {
			throw new CommonException(ErrorCode.CONFLICT_USER);
		}
		if (signUpChildRequest.residentNum().length() != 13) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		//----------예외처리 완료----------//
		try {
			String residentNum = addHyphenFromResidentNum(signUpChildRequest.residentNum());
			Gender gender = CommonUtil.getGender(residentNum);
			User parent = userRepository.findById(parentId)
				.orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));

			User child = User.builder()
				.username(signUpChildRequest.name())
				.authId(signUpChildRequest.authId())
				.password(parent.getPassword())
				.phone(null)
				.residentNum(residentNum)
				.pin(parent.getPin())
				.gender(gender)
				.build();

			User user = userRepository.save(child);
			Relationship relationship = Relationship.builder()
				.parent(parent)
				.child(user)
				.build();
			relationshipRepository.save(relationship);
			return SignUpChildResponse.builder()
				.code(201)
				.id(user.getId())
				.message("회원가입에 성공했습니다.")
				.build();

		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public PinValidResponse validatePin(AuthPinRequest authPinRequest, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));
		String requestPin = authPinRequest.pin();
		String userPin = user.getPin();
		if (!requestPin.equals(userPin)) {
			throw new CommonException(ErrorCode.INVALID_PIN);
		}
		return PinValidResponse.builder()
			.code(200)
			.message("요청을 성공했습니다.")
			.build();
	}

	public List<AuthListResponse> getAuthList(Long userId) {
		List<AuthListResponse> result = new ArrayList<>();
		List<Relationship> relationships = relationshipRepository.findAllByUserId(userId);
		if (relationships.isEmpty()) {
			User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER
			));
			result.add(AuthListResponse.of(userId, Role.INDIVIDUAL, user.getUsername(), user.getProfileLink(),
				user.getGender()));
			return result;
		}
		try {
			User parent = relationships.get(0).getParent();
			result.add(AuthListResponse.of(parent.getId(), Role.PARENT,
				parent.getUsername(), parent.getProfileLink(), parent.getGender()));

			for (Relationship relationship : relationships) {
				User child = relationship.getChild();
				result.add(AuthListResponse.of(child.getId(), Role.CHILD,
					child.getUsername(), child.getProfileLink(), child.getGender()));
			}
			return result;
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public JwtTokenDto switchAccount(Long userId, SwitchAccountRequest switchAccountRequest) {
		Long targetId = switchAccountRequest.targetId();
		List<Relationship> relationships = relationshipRepository.findAllByUserId(userId);
		if (relationships.isEmpty()) {
			throw new CommonException(ErrorCode.FORBIDDEN);
		}
		boolean hasRelationship = false;
		for (Relationship relationship : relationships) {
			if (relationship.getParent().getId().equals(targetId)) {
				hasRelationship = true;
			}
			if (relationship.getChild().getId().equals(targetId)) {
				hasRelationship = true;
			}
		}
		if (!hasRelationship) {
			throw new CommonException(ErrorCode.FORBIDDEN);
		}
		try {
			User target = userRepository.findById(targetId)
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
			String targetAccessToken = jwtUtil.generateAccessToken(target.getAuthId(), target.getId());
			String targetRefreshToken = jwtUtil.generateRefreshToken(target.getAuthId(), target.getId());
			return JwtTokenDto.of(targetAccessToken, targetRefreshToken, target.getId(), target.getUsername(),
				target.getProfileLink(), target.getGender(), target.getResidentNum().substring(0, 6));
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
