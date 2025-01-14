package com.hana4.sonjumoney.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.domain.enums.AccountType;
import com.hana4.sonjumoney.dto.TransactionDto;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;

	@Transactional
	public void makeTransferByUserId(TransactionDto transactionDto) {
		Account sender = accountRepository.findByUser_IdAndAccountType_AccountType(transactionDto.senderId(),
			AccountType.FREE_DEPOSIT).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Account receiver = accountRepository.findByUser_IdAndAccountType_AccountType(transactionDto.receiverId(),
			AccountType.FREE_DEPOSIT).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Long amount = transactionDto.amount();
		transfer(sender, receiver, amount);
	}

	@Transactional
	protected void transfer(Account sender, Account receiver, Long amount) {
		try {
			sender.withdraw(amount);
			receiver.deposit(amount);
			accountRepository.save(sender);
			accountRepository.save(receiver);
		} catch (CommonException e) {
			throw new CommonException(ErrorCode.TRANSACTION_FAILED);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

	}
}
