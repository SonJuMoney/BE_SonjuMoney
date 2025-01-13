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
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id2", nullable = false)
	private Account account2;

	@Column(name = "pay_day", nullable = false)
	private Integer payDay;

	@Column(name = "pay_amount", nullable = false)
	private Long payAmount;

	@Column(length = 300, nullable = false)
	private String message;

	@Builder
	public AutoTransfer(Account account, Account account2, Integer payDay, Long payAmount, String message) {
		this.account = account;
		this.account2 = account2;
		this.payDay = payDay;
		this.payAmount = payAmount;
		this.message = message;
	}

}
