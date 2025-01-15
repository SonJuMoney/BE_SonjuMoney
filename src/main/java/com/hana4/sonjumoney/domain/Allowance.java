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
@Table(name = "Allowance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Allowance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "allowance_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false)
	private Member sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false)
	private Member receiver;

	@Column(nullable = false)
	private Long amount;

	@Builder
	public Allowance(Member sender, Member receiver, Long amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}
}
