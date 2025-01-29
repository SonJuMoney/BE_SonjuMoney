package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.AccountProduct;
import com.hana4.sonjumoney.domain.enums.Bank;

import lombok.Builder;

@Builder
public record MockAccountResponse(
	Long mockacc_id,
	Bank bank,
	Long balance,
	@JsonProperty("account_name")
	AccountProduct accountName,
	@JsonProperty("account_num")
	String accountNumber
) {
	public static MockAccountResponse of(
		Long mockacc_id,
		Bank bank,
		Long balance,
		AccountProduct accountName,
		String accountNumber
	) {
		return MockAccountResponse.builder()
			.mockacc_id(mockacc_id)
			.bank(bank)
			.balance(balance)
			.accountName(accountName)
			.accountNumber(accountNumber)
			.build();
	}
}
