package com.hana4.sonjumoney.service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.domain.AccountType;
import com.hana4.sonjumoney.domain.AutoTransfer;
import com.hana4.sonjumoney.domain.MockAccount;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AccountProduct;
import com.hana4.sonjumoney.domain.enums.Bank;
import com.hana4.sonjumoney.dto.AllowanceDto;
import com.hana4.sonjumoney.dto.TransferDto;
import com.hana4.sonjumoney.dto.request.CreateSavingAccountRequest;
import com.hana4.sonjumoney.dto.response.AccountInfoResponse;
import com.hana4.sonjumoney.dto.response.CreateAccountResponse;
import com.hana4.sonjumoney.dto.response.CreateSavingAccountResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AccountRepository;
import com.hana4.sonjumoney.repository.AccountTypeRepository;
import com.hana4.sonjumoney.repository.AutoTransferRepository;
import com.hana4.sonjumoney.repository.MockAccountRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final MockAccountRepository mockAccountRepository;
	private final UserRepository userRepository;
	private final AccountTypeRepository accountTypeRepository;
	private final AutoTransferRepository autoTransferRepository;

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

	public CreateAccountResponse makeAccount(Long userId, Long mockaccId) {
		MockAccount mockAccount = mockAccountRepository.findById(mockaccId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

		/* user_id가 unique key 이므로 DB에 param으로 전달된 user_id를 갖는 Account 데이터가 있으면 잘못된 요청 */
		Optional<Account> savedAccount = accountRepository.findByUserId(userId);
		if (savedAccount.isPresent()) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}

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
		return CreateAccountResponse.of(200, "계좌 등록에 성공했습니다.");
	}

	public AccountInfoResponse getAccountByUserId(Long userId) {
		Account account = accountRepository.findByUser_IdAndAccountType_AccountProduct(userId,
			AccountProduct.FREE_DEPOSIT).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		return AccountInfoResponse.from(account);
	}

	public CreateSavingAccountResponse makeSavingAccount(CreateSavingAccountRequest request) {
		/* 자동이체 등록 적금 계좌인 경우 */
		if (request.autoTransferable()) {
			Account withdrawalAccount = accountRepository.findByUserId(request.withdrawalAccountId())
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
			Account depositAccount = accountRepository.findById(request.depositAccountId())
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
			AutoTransfer autoTransferSetting = AutoTransfer.builder()
				.withdrawalAccount(withdrawalAccount)
				.depositAccount(depositAccount)
				.payDay(request.payDay())
				.payAmount(request.payAmount())
				.message(request.message())
				.build();

			autoTransferRepository.save(autoTransferSetting);
		}

		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		AccountType accountType = accountTypeRepository.findById(request.accountTypeId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

		String randomAccountNum = makeRandomAccountNum();

		Account account = Account.builder()
			.accountType(accountType)
			.user(user)
			.bank(Bank.HANA)
			.holderResidentNum(user.getResidentNum())
			.accountNum(randomAccountNum)
			.accountPassword(request.accountPassword())
			.balance(0L)
			.build();

		accountRepository.save(account);
		return CreateSavingAccountResponse.of(200, "적금계좌 개설 완료");
	}

	private String makeRandomAccountNum() {
		/* 13자리 계좌번호 랜덤 생성 */
		long randomNumber = ThreadLocalRandom.current().nextLong((long)1e12, (long)1e13);
		return String.valueOf(randomNumber);
	}
}
