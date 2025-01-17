package com.hana4.sonjumoney.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "Auto_Transfer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoTransfer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auto_transfer_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "withdrawal_account_id", nullable = false)
	private Account witdrawalAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deposit_account_id", nullable = false)
	private Account depositAccount;

	@Column(name = "pay_day", nullable = false)
	private Integer payDay;

	@Column(name = "pay_amount", nullable = false)
	private Long payAmount;

	@Column(length = 300, nullable = false)
	private String message;

	@Builder
	public AutoTransfer(Account witdrawalAccount, Account depositAccount, Integer payDay, Long payAmount, String message) {
		this.witdrawalAccount = witdrawalAccount;
		this.depositAccount = depositAccount;
		this.payDay = payDay;
		this.payAmount = payAmount;
		this.message = message;
	}

}
