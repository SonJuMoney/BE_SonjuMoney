package com.hana4.sonjumoney.domain;

import com.hana4.sonjumoney.domain.enums.AccountProduct;
import com.hana4.sonjumoney.domain.enums.Bank;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Account_Type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_type_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Bank bank;

	@Column(name = "account_product", nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountProduct accountProduct;

	@Builder
	public AccountType(Bank bank, AccountProduct accountProduct) {
		this.bank = bank;
		this.accountProduct = accountProduct;
	}

}
