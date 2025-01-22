package com.hana4.sonjumoney.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.dto.request.CreateAccountRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingAccountRequest;
import com.hana4.sonjumoney.dto.request.SendMoneyRequest;
import com.hana4.sonjumoney.repository.AccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountControllerTest extends ControllerTest {

	@Autowired
	AccountRepository accountRepository;

	@Test
	@Transactional
	@DisplayName("계좌 등록 예외 처리 테스트")
	void makeAccountTest() throws Exception {
		String api = "/api/accounts";
		CreateAccountRequest request = CreateAccountRequest.builder()
			.mockaccId(1L)
			.userId(1L)
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("계좌 조회 테스트")
	void getAccountTest() throws Exception {
		String api = "/api/accounts";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.account_name").value("하나자유입출금통장"))
			.andExpect(jsonPath("$.bank").value("하나은행"))
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("적금 계좌 생성 테스트")
	void createSavingAccountTest() throws Exception {
		String api = "/api/accounts/savings";

		CreateSavingAccountRequest request = CreateSavingAccountRequest.builder()
			.message("착하게 자라야 한다~")
			.accountPassword("1234")
			.autoTransferable(true)
			.userId(3L)
			.payDay(3)
			.payAmount(50000L)
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("적금 계좌(들) 조회 테스트")
	void findSavingAccountsTest() throws Exception {
		String api = "/api/accounts/savings";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("적금 계좌 송금 테스트 user1 -> user3 (account1 -> account2)")
	void sendMoneyToSavingAccountTest() throws Exception {
		String accountId = "2";
		String api = "/api/accounts/savings/" + accountId + "/transfer";

		Account senderAccount = accountRepository.findByUserId(1L).get();
		Account recieverAccount = accountRepository.findById(Long.parseLong(accountId)).get();
		Long beforeSenderBalance = senderAccount.getBalance();
		Long beforeRecieverBalance = recieverAccount.getBalance();
		Long amount = 1000L;
		System.out.println(beforeSenderBalance);
		System.out.println(beforeRecieverBalance);

		SendMoneyRequest request = SendMoneyRequest.builder()
			.amount(amount)
			.password("123456")
			.message("돈 보낸다")
			.build();

		String requestBody = objectMapper.writeValueAsString(request);
		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.content(requestBody))
			.andExpect(status().isOk())
			.andDo(print());

		Long afterSenderBalance = senderAccount.getBalance();
		Long afterRecieverBalance = recieverAccount.getBalance();
		System.out.println(afterSenderBalance);
		System.out.println(afterRecieverBalance);

		assertThat(afterSenderBalance).isEqualTo(beforeSenderBalance - amount);
		assertThat(afterRecieverBalance).isEqualTo(beforeRecieverBalance + amount);
	}

	@Test
	@Transactional
	@DisplayName("특정 적금계좌 이체내역 조회")
	void getSavingAccountTransactionTest() throws Exception {
		mockMvc.perform(get("/api/accounts/savings/{account_id}", 2)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
