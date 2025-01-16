package com.hana4.sonjumoney.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.domain.MockAccount;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AccountProduct;
import com.hana4.sonjumoney.domain.enums.Bank;
import com.hana4.sonjumoney.dto.AllowanceDto;
import com.hana4.sonjumoney.dto.TransferDto;
import com.hana4.sonjumoney.dto.response.AccountResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AccountRepository;
import com.hana4.sonjumoney.repository.MockAccountRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final MockAccountRepository mockAccountRepository;
	private final UserRepository userRepository;

	@Transactional
	public void makeTransferByUserId(AllowanceDto allowanceDto) {
		Account sender = accountRepository.findByUser_IdAndAccountType_AccountProduct(allowanceDto.senderId(),
			AccountProduct.FREE_DEPOSIT).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Account receiver = accountRepository.findByUser_IdAndAccountType_AccountProduct(allowanceDto.receiverId(),
			AccountProduct.FREE_DEPOSIT).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Long amount = allowanceDto.amount();
		transfer(TransferDto.of(sender, receiver, amount));
	}

	@Transactional
	protected void transfer(TransferDto transferDto) {
		try {
			Account sender = transferDto.sender();
			Account receiver = transferDto.receiver();

			sender.withdraw(transferDto.amount());
			receiver.deposit(transferDto.amount());
		} catch (CommonException e) {
			throw new CommonException(ErrorCode.TRANSACTION_FAILED);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

	}

	public AccountResponse makeAccount(Long userId, Long mockaccId) {
		MockAccount mockAccount = mockAccountRepository.findById(mockaccId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

		Account account = Account.builder()
			.accountType(mockAccount.getAccountType())
			.user(user)
			.bank(Bank.HANA)
			.holderResidentNum(user.getResidentNum())
			.accountNum(mockAccount.getAccountNum())
			.accountPassword(mockAccount.getAccountPassword())
			.balance(mockAccount.getBalance())
			.build();

		accountRepository.save(account);
		return AccountResponse.of(200, "계좌 등록에 성공했습니다.");
	}
}
