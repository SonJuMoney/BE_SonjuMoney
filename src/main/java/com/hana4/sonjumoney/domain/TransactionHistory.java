package com.hana4.sonjumoney.domain;

import com.hana4.sonjumoney.domain.enums.TransactionType;

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
@Table(name = "Transaction_History")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionHistory extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@Column(nullable = false)
	private Integer amount;

	@Column(length = 20, nullable = false)
	private String message;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Builder
	public TransactionHistory(Account account, Integer amount, String message, TransactionType transactionType) {
		this.account = account;
		this.amount = amount;
		this.message = message;
		this.transactionType = transactionType;
	}

}
