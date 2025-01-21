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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Alarm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@Builder
	public Alarm(User user, AlarmType alarmType, Long linkId, String message) {
		this.user = user;
		this.alarmStatus = AlarmStatus.RECEIVED;
		this.alarmType = alarmType;
		this.linkId = linkId;
		this.message = message;
	}
}
