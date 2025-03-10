package com.hana4.sonjumoney.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.dto.request.CreateAccountRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingAccountRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingsMessageRequest;
import com.hana4.sonjumoney.dto.request.SendMoneyRequest;
import com.hana4.sonjumoney.dto.request.SignInRequest;
import com.hana4.sonjumoney.repository.AccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountControllerTest extends ControllerTest {

	@Autowired
	AccountRepository accountRepository;

	@Test
	@Transactional
	@DisplayName("계좌 등록 테스트")
	void makeAccountTest() throws Exception {
		SignInRequest signInRequest = new SignInRequest("test5", "1234");
		MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);
		accessToken = responseMap.get("access_token");
		loginUserId = String.valueOf(responseMap.get("user_id"));
		System.out.println("accessToken:" + accessToken);

		String api = "/api/v1/accounts";
		CreateAccountRequest request = CreateAccountRequest.builder()
			.mockaccId(3L)
			.userId(5L)
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("자녀 계좌 등록 테스트")
	void makeChildAccountTest() throws Exception {
		String api = "/api/v1/accounts";
		CreateAccountRequest request = CreateAccountRequest.builder()
			.mockaccId(3L)
			.userId(5L)
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("계좌 등록 예외 처리 테스트")
	void makeAccountExceptionTest() throws Exception {
		String api = "/api/v1/accounts";
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
		String api = "/api/v1/accounts";

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
		String api = "/api/v1/accounts/savings";

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
		String api = "/api/v1/accounts/savings";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.is_child").exists())
			.andExpect(jsonPath("$.savings").isEmpty())
			.andDo(print());
	}

	@Test
	@DisplayName("아이일 경우 적금 계좌(들) 조회 테스트")
	void findSavingAccountsChildTest() throws Exception {
		SignInRequest signInRequest = new SignInRequest("test7", "1234");
		MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);
		accessToken = responseMap.get("access_token");

		String api = "/api/v1/accounts/savings";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.is_child").value(true))
			.andExpect(jsonPath("$.savings").doesNotExist())
			.andDo(print());
	}

	@Test
	@DisplayName("적금 계좌(들) 조회 empty 테스트")
	void findSavingAccountsEmptyTest() throws Exception {
		SignInRequest signInRequest = new SignInRequest("test2", "1234");
		MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);
		accessToken = responseMap.get("access_token");

		String api = "/api/v1/accounts/savings";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.is_child").value(false))
			.andExpect(jsonPath("$.savings").isEmpty())
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("적금 계좌 송금 테스트 user1 -> user3 (account1 -> account2)")
	void sendMoneyToSavingAccountTest() throws Exception {
		String accountId = "2";
		String api = "/api/v1/accounts/savings/" + accountId + "/transfer";

		Account senderAccount = accountRepository.findByUserId(1L).get();
		Account recieverAccount = accountRepository.findById(Long.parseLong(accountId)).get();
		Long beforeSenderBalance = senderAccount.getBalance();
		Long beforeRecieverBalance = recieverAccount.getBalance();
		Long amount = 1000L;

		SendMoneyRequest request = SendMoneyRequest.builder()
			.amount(amount)
			.pin("123456")
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

		assertThat(afterSenderBalance).isEqualTo(beforeSenderBalance - amount);
		assertThat(afterRecieverBalance).isEqualTo(beforeRecieverBalance + amount);
	}

	@Test
	@Transactional
	@DisplayName("적금 계좌 송금 pin 오류 테스트")
	void transferToSavingAccountInvalidPinTest() throws Exception {
		String accountId = "2";
		String api = "/api/v1/accounts/savings/" + accountId + "/transfer";
		Long amount = 1000L;

		SendMoneyRequest request = SendMoneyRequest.builder()
			.amount(amount)
			.pin("124353")
			.message("돈 보낸다")
			.build();

		String requestBody = objectMapper.writeValueAsString(request);
		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.content(requestBody))
			.andExpect(status().is4xxClientError())
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("무인증 적금 계좌 송금 요청에 대한 예외 테스트")
	void statusFalseTransferTest() throws Exception {
		String accountId = "2";
		String api = "/api/v1/accounts/savings/" + accountId + "/transfer";

		Long amount = 1000L;

		SendMoneyRequest request = SendMoneyRequest.builder()
			.amount(amount)
			.pin("010101")
			.message("돈 보낸다")
			.build();

		String requestBody = objectMapper.writeValueAsString(request);
		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.content(requestBody))
			.andExpect(status().is4xxClientError())
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("특정 적금계좌 이체내역 조회")
	void getSavingAccountTransactionTest() throws Exception {
		Integer page = 0;

		mockMvc.perform(get("/api/v1/accounts/savings/{account_id}?page=" + page, 2)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());

	}

	@Test
	@Transactional
	@DisplayName("적금 납입 한도 조회")
	void getSavingAccountLimitTest() throws Exception {
		mockMvc.perform(get("/api/v1/accounts/savings/{account_id}/limit", 2)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("내 계좌 이체 내역 조회")
	void getTransactionsTest() throws Exception {
		Integer page = 0;

		mockMvc.perform(get("/api/v1/accounts/{account_id}/transactions?page=" + page, 1)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("적금 메세지 생성")
	void createSavingsMessage() throws Exception {
		CreateSavingsMessageRequest messageRequest = CreateSavingsMessageRequest.builder()
			.Message("우리 손주. 할아버지야. 건강하게 자라렴")
			.build();
		mockMvc.perform(post("/api/v1/accounts/savings/{auto_transfer_id}/message", 1)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(messageRequest)))
			.andExpect(status().isCreated())
			.andDo(print());
	}
}
