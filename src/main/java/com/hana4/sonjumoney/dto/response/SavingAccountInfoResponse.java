package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.Bank;

import lombok.Builder;

@Builder
public record SavingAccountInfoResponse(
	@JsonProperty("account_id")
	Long accountId,
	@JsonProperty("account_name")
	String accountName,
	@JsonProperty("user_name")
	String userName,
	@JsonProperty("bank")
	Bank bank,
	@JsonProperty("account_num")
	String accountNum,
	@JsonProperty("balance")
	Long balance
) {
	public static SavingAccountInfoResponse of(Long accountId, String accountName, String userName, Bank bank,
		String accountNum,
		Long balance) {
		return SavingAccountInfoResponse.builder()
			.accountId(accountId)
			.accountName(accountName)
			.userName(userName)
			.bank(bank)
			.accountNum(accountNum)
			.balance(balance)
			.build();
	}
}
