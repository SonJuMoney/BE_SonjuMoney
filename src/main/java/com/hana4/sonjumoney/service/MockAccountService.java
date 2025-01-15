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

	public List<MockAccountResponse> findMyMockAccounts(Long userId){
		Optional<User> oUser = userRepository.findByUserId(userId);
		User user = oUser.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		String residentNum = user.getResidentNum();

		List<MockAccount> mockAccounts = mockAccountRepository.findUserMockAccounts(residentNum);

		List<MockAccountResponse> response = new ArrayList<>();
		for(MockAccount mockAccount : mockAccounts){
			response.add(MockAccountResponse.of(mockAccount.getId(), Bank.HANA, mockAccount.getBalance(), AccountProduct.FREE_DEPOSIT,
				mockAccount.getAccountNum()));
		}

		return response;
	}

	public List<MockAccountResponse> findChildMockAccounts(Long userId, Long childId){
		Optional<User> oParent = userRepository.findByUserId(userId);
		User parent = oParent.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		String parentResidentNum = parent.getResidentNum();

		Optional<User> oChild = userRepository.findByUserId(childId);
		User child = oChild.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		String childResidentNum = child.getResidentNum();

		List<MockAccount> mockAccounts = mockAccountRepository.findChildMockAccounts(parentResidentNum, childResidentNum);

		List<MockAccountResponse> response = new ArrayList<>();
		for(MockAccount mockAccount : mockAccounts){
			response.add(MockAccountResponse.of(mockAccount.getId(), Bank.HANA, mockAccount.getBalance(), AccountProduct.FREE_DEPOSIT,
				mockAccount.getAccountNum()));
		}

		return response;
	}

}
