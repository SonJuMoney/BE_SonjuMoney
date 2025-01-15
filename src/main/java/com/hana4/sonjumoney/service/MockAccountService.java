package com.hana4.sonjumoney.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.MockAccount;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AccountProduct;
import com.hana4.sonjumoney.domain.enums.Bank;
import com.hana4.sonjumoney.dto.response.MockAccountResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.MockAccountRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MockAccountService {

	private final MockAccountRepository mockAccountRepository;
	private final UserRepository userRepository;

	public List<MockAccountResponse> findMyMockAccounts(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		String residentNum = user.getResidentNum();

		List<MockAccount> mockAccounts;
		try {
			mockAccounts = mockAccountRepository.findUserMockAccounts(residentNum);
		} catch (CommonException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}

		return makeMockAccountResponse(mockAccounts);
	}

	public List<MockAccountResponse> findChildMockAccounts(Long userId, Long childId) {
		User parent = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		String parentResidentNum = parent.getResidentNum();

		User child = userRepository.findById(childId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		String childResidentNum = child.getResidentNum();

		List<MockAccount> mockAccounts;
		try {
			mockAccounts = mockAccountRepository.findChildMockAccounts(parentResidentNum,
				childResidentNum);
		} catch (CommonException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}

		return makeMockAccountResponse(mockAccounts);
	}

	private List<MockAccountResponse> makeMockAccountResponse(List<MockAccount> mockAccounts){
		List<MockAccountResponse> response = new ArrayList<>();
		for (MockAccount mockAccount : mockAccounts) {
			response.add(MockAccountResponse.of(mockAccount.getId(), Bank.HANA, mockAccount.getBalance(),
				AccountProduct.FREE_DEPOSIT,
				mockAccount.getAccountNum()));
		}

		return response;
	}
}
