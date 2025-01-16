package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.domain.enums.Bank;

import lombok.Builder;

@Builder
public record AccountInfoResponse(
	@JsonProperty("account_name")
	String accountName,
	String bank,
	@JsonProperty("account_num")
	String accountNum,
	Long balance
) {
	public static AccountInfoResponse of(String accountName, Bank bank,String accountNum,Long balance) {
		return AccountInfoResponse.builder()
			.accountName(accountName)
			.bank(bank.getValue())
			.accountNum(accountNum)
			.balance(balance)
			.build();
	}

	public static AccountInfoResponse from(Account account) {
		String name = account.getBank().getValue() + account.getAccountType().getAccountProduct().getName() + "통장";
		String bank = account.getBank().getValue() + "은행";
		return AccountInfoResponse.builder()
			.accountName(name)
			.bank(bank)
			.accountNum(account.getAccountNum())
			.balance(account.getBalance())
			.build();
	}
}
