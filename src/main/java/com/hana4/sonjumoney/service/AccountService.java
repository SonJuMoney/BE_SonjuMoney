package com.hana4.sonjumoney.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.domain.AccountType;
import com.hana4.sonjumoney.domain.AutoTransfer;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.MockAccount;
import com.hana4.sonjumoney.domain.TransactionHistory;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AccountProduct;
import com.hana4.sonjumoney.domain.enums.Bank;
import com.hana4.sonjumoney.domain.enums.TransactionType;
import com.hana4.sonjumoney.dto.AllowanceDto;
import com.hana4.sonjumoney.dto.SavingAccountContentDto;
import com.hana4.sonjumoney.dto.SavingAccountResultDto;
import com.hana4.sonjumoney.dto.SavingAccountTransactionDto;
import com.hana4.sonjumoney.dto.TransactionHistoryContentsDto;
import com.hana4.sonjumoney.dto.TransactionHistoryDto;
import com.hana4.sonjumoney.dto.TransactionHistoryResultDto;
import com.hana4.sonjumoney.dto.TransactionHistoryTransactionsDto;
import com.hana4.sonjumoney.dto.TransferDto;
import com.hana4.sonjumoney.dto.request.AuthPinRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingAccountRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingsMessageRequest;
import com.hana4.sonjumoney.dto.request.SendMoneyRequest;
import com.hana4.sonjumoney.dto.response.AccountInfoResponse;
import com.hana4.sonjumoney.dto.response.CreateAccountResponse;
import com.hana4.sonjumoney.dto.response.CreateSavingAccountResponse;
import com.hana4.sonjumoney.dto.response.CreateSavingsMessageResponse;
import com.hana4.sonjumoney.dto.response.GetSavingAccountLimitResponse;
import com.hana4.sonjumoney.dto.response.GetSavingAccountResponse;
import com.hana4.sonjumoney.dto.response.GetTransactionHistoryResponse;
import com.hana4.sonjumoney.dto.response.SavingAccountInfoResponse;
import com.hana4.sonjumoney.dto.response.SavingAccountResponse;
import com.hana4.sonjumoney.dto.response.TransferResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.AccountRepository;
import com.hana4.sonjumoney.repository.AccountTypeRepository;
import com.hana4.sonjumoney.repository.AutoTransferRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.MockAccountRepository;
import com.hana4.sonjumoney.repository.TransactionHistoryRepository;
import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final MockAccountRepository mockAccountRepository;
	private final UserRepository userRepository;
	private final AccountTypeRepository accountTypeRepository;
	private final AutoTransferRepository autoTransferRepository;
	private final TransactionHistoryRepository transactionHistoryRepository;
	private final MemberRepository memberRepository;

	private final AuthService authService;

	private final Integer PAGE_SIZE = 20;
	private final RedisService redisService;

	@Transactional
	public void makeTransferByUserId(AllowanceDto allowanceDto) {
		Account sender = accountRepository.findByUser_IdAndAccountType_AccountProduct(allowanceDto.senderId(),
			AccountProduct.FREE_DEPOSIT).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Account receiver = accountRepository.findByUser_IdAndAccountType_AccountProduct(allowanceDto.receiverId(),
			AccountProduct.FREE_DEPOSIT).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Long amount = allowanceDto.amount();
		transfer(TransferDto.of(sender, receiver, amount));

		makeTransactionHistory(TransactionHistoryDto.builder()
			.account(sender)
			.afterBalance(sender.getBalance())
			.opponentAccountId(receiver.getId())
			.message(allowanceDto.message())
			.transactionType(TransactionType.WITHDRAW)
			.amount(allowanceDto.amount())
			.build());

		makeTransactionHistory(TransactionHistoryDto.builder()
			.account(receiver)
			.afterBalance(sender.getBalance())
			.opponentAccountId(sender.getId())
			.message(allowanceDto.message())
			.transactionType(TransactionType.DEPOSIT)
			.amount(allowanceDto.amount())
			.build());
	}

	public CreateAccountResponse makeAccount(Long userId, Long mockaccId) {
		MockAccount mockAccount = mockAccountRepository.findById(mockaccId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

		/* user_id가 unique key 이므로 DB에 param으로 전달된 user_id를 갖는 Account 데이터가 있으면 잘못된 요청 */
		Optional<Account> savedAccount = accountRepository.findByUserId(userId);
		if (savedAccount.isPresent()) {
			throw new CommonException(ErrorCode.ALREADY_EXIST_ACCOUNT);
		}

		Account account = Account.builder()
			.accountType(mockAccount.getAccountType())
			.user(user)
			.bank(Bank.HANA)
			.holderResidentNum(user.getResidentNum())
			.deputyResidentNum(mockAccount.getDeputyResidentNum())
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

	@Transactional
	public CreateSavingAccountResponse makeSavingAccount(CreateSavingAccountRequest request, Long parentId) {
		/* 자동이체 등록 적금 계좌인 경우 */
		if (request.autoTransferable()) {
			Account withdrawalAccount = accountRepository.findByUserId(parentId)
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
			Account depositAccount = accountRepository.findByUserId(request.userId())
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

			/* 입,출금 계좌가 동일한 경우 예외처리 */
			if (depositAccount.getId().equals(withdrawalAccount.getId())) {
				throw new CommonException(ErrorCode.SAME_ACCOUNT);
			}

			AutoTransfer autoTransferSetting = AutoTransfer.builder()
				.withdrawalAccount(withdrawalAccount)
				.depositAccount(depositAccount)
				.payDay(request.payDay())
				.payAmount(request.payAmount())
				.message(request.message())
				.build();

			autoTransferRepository.save(autoTransferSetting);
		}

		final Long accountTypeId = 2L;
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
		AccountType accountType = accountTypeRepository.findById(accountTypeId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		User parent = userRepository.findById(parentId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

		String randomAccountNum = makeRandomAccountNum();

		Account account = Account.builder()
			.accountType(accountType)
			.user(user)
			.bank(Bank.HANA)
			.holderResidentNum(user.getResidentNum())
			.deputyResidentNum(parent.getResidentNum())
			.accountNum(randomAccountNum)
			.accountPassword(request.accountPassword())
			.balance(0L)
			.build();

		accountRepository.save(account);
		return CreateSavingAccountResponse.of(200, "적금계좌 개설 완료");
	}

	public SavingAccountResponse findSavingAccounts(Long userId) {
		/* 처음 회원가입한 사람은 가족 설정이 안되어있으므로 MemberResponse가 Empty */
		// List<Member> newUserMember = memberRepository.findAllByUserId(userId);
		// if (newUserMember.isEmpty()) {
		// 	return SavingAccountResponse.of(false, null);
		// }

		/* members -> empty = 모든 family에서 son or daughter인 경우 */
		List<Member> members = memberRepository.findMemberByMemberRoleAndUserId(userId);
		if (members.isEmpty()) {
			return SavingAccountResponse.of(true, null);
		}

		List<Account> accounts = accountRepository.findSavingAccountsByUserIdAAndAccountType_AccountProduct(userId,
			AccountProduct.MY_SAVINGS).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

		List<SavingAccountInfoResponse> response = accounts.stream()
			.map(account -> SavingAccountInfoResponse.of(account.getId(),
				account.getAccountType().getAccountProduct().getName(), account.getUser().getUsername(), Bank.HANA,
				account.getAccountNum(),
				account.getBalance())).toList();

		return SavingAccountResponse.of(false, response);
	}

	@Transactional
	public TransferResponse sendMoneyProcess(Long accountId, SendMoneyRequest request, Long userId) {
		/* 비밀번호 인증 여부가 false면 송금 process를 수행하지 않음 */
		authService.validatePin(new AuthPinRequest(request.pin()), userId);
		Account senderAccount = accountRepository.findByUserId(userId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Account receiverAccount = accountRepository.findById(accountId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

		transfer(TransferDto.of(senderAccount, receiverAccount, request.amount()));

		TransactionHistoryDto senderHistoryDto = TransactionHistoryDto.builder()
			.account(senderAccount)
			.amount(request.amount())
			.message(request.message())
			.afterBalance(senderAccount.getBalance())
			.transactionType(TransactionType.WITHDRAW)
			.opponentAccountId(receiverAccount.getId())
			.build();

		TransactionHistoryDto receiverHistoryDto = TransactionHistoryDto.builder()
			.account(receiverAccount)
			.amount(request.amount())
			.message(request.message())
			.afterBalance(receiverAccount.getBalance())
			.transactionType(TransactionType.DEPOSIT)
			.opponentAccountId(senderAccount.getId())
			.build();

		makeTransactionHistory(senderHistoryDto);
		makeTransactionHistory(receiverHistoryDto);
		return TransferResponse.of(201, "입,출금 완료");
	}

	@Transactional
	protected void transfer(TransferDto transferDto) {
		try {
			Account sender = transferDto.sender();
			Account receiver = transferDto.receiver();

			sender.withdraw(transferDto.amount());
			receiver.deposit(transferDto.amount());
		} catch (Exception e) {
			throw new CommonException(ErrorCode.TRANSACTION_FAILED);
		}

	}

	@Transactional
	protected void makeTransactionHistory(TransactionHistoryDto dto) {
		/* 한번의 송금으로 2개의 거래내역 생성 */
		TransactionHistory history = TransactionHistory.builder()
			.account(dto.account())
			.amount(dto.amount())
			.message(dto.message())
			.afterBalance(dto.afterBalance())
			.transactionType(dto.transactionType())
			.opponentAccountId(dto.opponentAccountId())
			.build();

		transactionHistoryRepository.save(history);
	}

	private String makeRandomAccountNum() {
		while (true) {
			/* 13자리 계좌번호 랜덤 생성 */
			long randomNumber = ThreadLocalRandom.current().nextLong((long)1e12, (long)1e13);
			String randomAccountNum = String.valueOf(randomNumber);

			/* 계좌번호 중복 체크 */
			Optional<Account> account = accountRepository.findByHolderResidentNum(randomAccountNum);
			if (account.isEmpty())
				return randomAccountNum;
		}
	}

	/*특정 적금계좌 이체내역 조회*/
	public GetSavingAccountResponse getSavingAccount(Long userId, Long opponentAccountId, Integer page) {
		PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
		Account userAccount = accountRepository.findByUserId(userId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

		List<LocalDate> dateList;
		try {
			dateList = transactionHistoryRepository.findDistinctSavingsDatesAsObjects(userAccount.getId(),
					opponentAccountId, pageRequest)
				.stream()
				.map(result -> ((Date)result[0]).toLocalDate())
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		List<TransactionHistory> transactionHistories;
		try {
			transactionHistories = transactionHistoryRepository.findSavingsByDates(
				userAccount.getId(), opponentAccountId, dateList);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		if (transactionHistories.isEmpty()) {
			return GetSavingAccountResponse.builder()
				.isSuccess(true)
				.code(200)
				.message("요청 성공")
				.result(SavingAccountResultDto.builder()
					.hasNext(false)
					.page(page)
					.contents(new ArrayList<>())
					.build())
				.build();
		}

		List<SavingAccountContentDto> contents = new ArrayList<>();

		Boolean hasNext;
		try {
			hasNext = transactionHistoryRepository.hasNextSavings(userAccount.getId(), opponentAccountId,
				transactionHistories.get(transactionHistories.size() - 1).getId());
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		for (LocalDate localDate : dateList) {
			List<SavingAccountTransactionDto> transactions = transactionHistories.stream()
				.filter(transaction -> transaction.getCreatedAt().toLocalDate().equals(localDate))
				.map(transaction -> SavingAccountTransactionDto.of(
					userAccount.getUser().getUsername(),
					transaction.getCreatedAt(),
					transaction.getMessage(),
					transaction.getAmount()))
				.collect(Collectors.toList());

			contents.add(SavingAccountContentDto.of(localDate, transactions));
		}
		contents.sort(Comparator.comparing(SavingAccountContentDto::date).reversed());

		SavingAccountResultDto result = SavingAccountResultDto.of(hasNext, page, contents);
		return GetSavingAccountResponse.of(true, 200, "요청 성공", result);
	}

	public GetSavingAccountLimitResponse getSavingAccountLimit(Long userId, Long opponentAccountId) {
		Long totalPayment;
		Integer monthPayment;
		LocalDateTime startOfDay = YearMonth.now().atDay(1).atStartOfDay();
		LocalDateTime endOfDay = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);

		Account userAccount = accountRepository.findByUserId(userId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Account opponentAccount = accountRepository.findById(opponentAccountId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

		try {
			totalPayment = transactionHistoryRepository.getTotalPayment(userAccount.getId(), opponentAccountId);
			if (totalPayment == null) {
				totalPayment = 0L;
			}
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		try {
			monthPayment = transactionHistoryRepository.getCurrentMonthPayment(userAccount.getId(), opponentAccountId,
				startOfDay, endOfDay);
			if (monthPayment == null) {
				monthPayment = 0;
			}
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		Integer availableAmount = Math.max(500000 - monthPayment, 0);

		return GetSavingAccountLimitResponse.of(
			opponentAccount.getUser().getUsername(),
			totalPayment,
			monthPayment,
			availableAmount
		);

	}

	public GetTransactionHistoryResponse getTransactions(Long userId, Long accountId, Integer page) {
		PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
		Account account = accountRepository.findByIdAndUserId(accountId, userId)
			.orElseThrow(() -> new CommonException(ErrorCode.BAD_REQUEST));
		List<LocalDate> dateList;
		try {
			dateList = transactionHistoryRepository.findDistinctDatesAsObjects(account.getId(),
					pageRequest)
				.stream()
				.map(result -> ((Date)result[0]).toLocalDate())
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		List<TransactionHistory> myTransactions;
		try {
			myTransactions = transactionHistoryRepository.findByDates(account.getId(), dateList);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		if (myTransactions.isEmpty()) {
			return GetTransactionHistoryResponse.builder()
				.isSuccess(true)
				.code(200)
				.message("요청 성공")
				.result(TransactionHistoryResultDto.builder()
					.hasNext(false)
					.page(page)
					.contents(new ArrayList<>())
					.build())
				.build();
		}
		List<TransactionHistoryContentsDto> contents = new ArrayList<>();
		Boolean hasNext;
		try {
			hasNext = transactionHistoryRepository.hasNext(account.getId(),
				myTransactions.get(myTransactions.size() - 1).getId());
		} catch (Exception e) {
			throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		for (LocalDate localDate : dateList) {
			List<TransactionHistoryTransactionsDto> transactions = myTransactions.stream()
				.filter(content -> content.getCreatedAt().toLocalDate().equals(localDate))
				.map(content -> TransactionHistoryTransactionsDto.of(
					content.getMessage(),
					content.getTransactionType(),
					content.getAfterBalance(),
					content.getCreatedAt(),
					content.getAmount()))
				.collect(Collectors.toList());

			contents.add(TransactionHistoryContentsDto.of(localDate, transactions));
		}
		contents.sort(Comparator.comparing(TransactionHistoryContentsDto::date).reversed());
		TransactionHistoryResultDto result = TransactionHistoryResultDto.of(hasNext, page, contents);
		return GetTransactionHistoryResponse.of(true, 200, "요청 성공", result);

	}

	public CreateSavingsMessageResponse createSavingsMessage(Long userId, Long autoTransferId,
		CreateSavingsMessageRequest messageRequest) {
		Account account = accountRepository.findByUserId(userId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

		AutoTransfer autoTransfer = autoTransferRepository.findByIdAndWithdrawalAccountId(autoTransferId,
				account.getId())
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));

		String savedMessage = redisService.createSavingsMessage(account.getId(), autoTransferId,
			messageRequest.Message());

		return CreateSavingsMessageResponse.of(201,
			savedMessage);
	}
}
