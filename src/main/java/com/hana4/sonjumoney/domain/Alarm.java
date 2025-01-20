package com.hana4.sonjumoney.domain;

import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.domain.enums.AlarmType;

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
import lombok.Getter;

@Entity
@Getter
@Table(name = "Alarm")
public class Alarm extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "alarm_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private AlarmStatus alarmStatus;

	@Column(name = "alarm_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private AlarmType alarmType;

	@Column(name = "link_id", nullable = false)
	private Long linkId;

	@Column(length = 300, nullable = false)
	private String message;

	public void changeStatusToChecked() {
		this.alarmStatus = AlarmStatus.CHECKED;
	}

	public void changeStatusReceived() {
		this.alarmStatus = AlarmStatus.RECEIVED;
	}
}
