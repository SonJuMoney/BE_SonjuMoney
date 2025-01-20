package com.hana4.sonjumoney.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.MockAccount;
import com.hana4.sonjumoney.domain.Relationship;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AccountProduct;
import com.hana4.sonjumoney.domain.enums.Bank;
import com.hana4.sonjumoney.dto.request.PinValidRequest;
import com.hana4.sonjumoney.dto.response.MockAccountResponse;
import com.hana4.sonjumoney.dto.response.PinValidResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.MockAccountRepository;
import com.hana4.sonjumoney.repository.RelationshipRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MockAccountService {

	private final MockAccountRepository mockAccountRepository;
	private final UserRepository userRepository;
	private final RelationshipRepository relationshipRepository;

	public List<MockAccountResponse> findMyMockAccounts(Long userId) {
		return findMockAccount(userId);
	}

	public List<MockAccountResponse> findChildMockAccounts(Long userId, Long childId) {
		User parent = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		String parentResidentNum = parent.getResidentNum();

		User child = userRepository.findById(childId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		String childResidentNum = child.getResidentNum();

		/* validate relationship */
		Optional<Relationship> relationship = relationshipRepository.findRelationshipByChildIdAndParentId(
			userId, childId);
		if (relationship.isEmpty())
			throw new CommonException(ErrorCode.DIFFERENT_FAMILY);

		return findMockAccount(childId);
	}

	public PinValidResponse checkMockAccountPin(PinValidRequest request) {
		MockAccount mockAccount = mockAccountRepository.findById(request.mockaccId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		if (!mockAccount.getAccountPassword().equals(request.pin())) {
			throw new CommonException(ErrorCode.INVALID_PIN);
		}

		return PinValidResponse.of(200, "Mock계좌 비밀번호 일치");
	}

	private List<MockAccountResponse> makeMockAccountResponse(List<MockAccount> mockAccounts) {
		List<MockAccountResponse> response = new ArrayList<>();
		for (MockAccount mockAccount : mockAccounts) {
			response.add(MockAccountResponse.of(mockAccount.getId(), Bank.HANA, mockAccount.getBalance(),
				AccountProduct.FREE_DEPOSIT,
				mockAccount.getAccountNum()));
		}

		return response;
	}

	private List<MockAccountResponse> findMockAccount(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		String residentNum = user.getResidentNum();
		List<MockAccount> mockAccounts;
		try {
			mockAccounts = mockAccountRepository.findUserMockAccounts(residentNum);
		} catch (CommonException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}

		return makeMockAccountResponse(mockAccounts);
	}
}
