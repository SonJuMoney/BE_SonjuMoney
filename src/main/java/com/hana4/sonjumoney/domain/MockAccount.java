package com.hana4.sonjumoney.domain;

import com.hana4.sonjumoney.domain.enums.Bank;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "MockAccount")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MockAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mockacc_id", nullable = false)
	private Long id;

	//계좌유형
	@ManyToOne
	@JoinColumn(name = "account_type_id");
	private AccountType accountType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Bank bank;

	@Column(name = "account_num", nullable = false)
	private String accountNum;

	@Column(name = "holder_resident_num", nullable = false)
	private String holderResidentNum;

	@Column(name = "deputy_resident_num", nullable = false)
	private String DeputyResidentNum;

	@Column(nullable = false)
	private Long balance;

	@Column(name = "account_password", nullable = false)
	private String accountPassword;

	@Builder
	public MockAccount(AccountType accountType,Bank bank, String accountNum, String holderResidentNum, String deputyResidentNum, Long balance,
		String accountPassword) {
		this.accountType = accountType;
		this.bank = bank;
		this.accountNum = accountNum;
		this.holderResidentNum = holderResidentNum;
		this.DeputyResidentNum = deputyResidentNum;
		this.balance = balance;
		this.accountPassword = accountPassword;

	}

}
