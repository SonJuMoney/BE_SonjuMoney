package com.hana4.sonjumoney.domain;

import com.hana4.sonjumoney.domain.enums.Bank;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "Account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_type_id", nullable = false)
	private AccountType accountType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Bank bank;

	@Column(name = "holder_resident_num", length = 14, nullable = false)
	private String holderResidentNum;

	@Column(name = "deputy_resident_num", length = 14, nullable = false)
	private String deputyResidentNum;

	@Column(name = "account_num", length = 20, nullable = false)
	private String accountNum;

	@Column(name = "account_password", length = 4, nullable = false)
	private String accountPassword;

	@Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
	private Long balance;

	@Builder
	public Account(AccountType accountType, User user, Bank bank, String holderResidentNum, String deputyResidentNum,
		String accountNum,
		String accountPassword, Long balance) {
		this.accountType = accountType;
		this.user = user;
		this.bank = bank;
		this.holderResidentNum = holderResidentNum;
		this.deputyResidentNum = deputyResidentNum;
		this.accountNum = accountNum;
		this.accountPassword = accountPassword;
		this.balance = balance;
	}

	public void withdraw(Long amount) {
		if (balance.compareTo(amount) < 0) {
			throw new CommonException(ErrorCode.INSUFFICIENT_BALANCE);
		}
		this.balance -= amount;
	}

	public void deposit(Long amount) {
		this.balance += amount;
	}

}
