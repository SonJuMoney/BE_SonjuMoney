package com.hana4.sonjumoney.domain;

import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.domain.enums.AlarmType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "Alarm")
public class Alarm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long alarmId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User userId;

	@Column(nullable = false)
	private AlarmStatus alarmStatus;

	@Column(nullable = false)
	private AlarmType alarmType;

	@Column(nullable = false)
	private Long linkId;

	@Column(length = 300, nullable = false)
	private String message;
}
