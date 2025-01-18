package com.hana4.sonjumoney.domain;

import java.time.LocalDateTime;

import com.hana4.sonjumoney.domain.enums.AllDayStatus;
import com.hana4.sonjumoney.domain.enums.EventCategory;
import com.hana4.sonjumoney.domain.enums.NotifyStatus;

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
@Table(name = "Event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "family_id", nullable = false)
	private Family family;

	@Column(name = "event_name", length = 30, nullable = false)
	private String eventName;

	@Column(name = "start_date_time", nullable = false, columnDefinition = "DATE")
	private LocalDateTime startDateTime;

	@Column(name = "end_date_time", nullable = false, columnDefinition = "DATE")
	private LocalDateTime endDateTime;

	@Column(name = "event_category", nullable = false)
	@Enumerated(EnumType.STRING)
	private EventCategory eventCategory;

	@Column(name = "notify_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotifyStatus notifyStatus;

	@Column(name = "all_day_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private AllDayStatus allDayStatus;

	@Builder
	public Event(Family family, String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime,
		EventCategory eventCategory,
		NotifyStatus status, AllDayStatus allDayStatus) {
		this.family = family;
		this.eventName = eventName;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.eventCategory = eventCategory;
		this.notifyStatus = status;
		this.allDayStatus = allDayStatus;
	}

	public void updateEvent(EventCategory eventCategory, String eventName, LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		AllDayStatus allDayStatus) {
		this.eventCategory = eventCategory;
		this.eventName = eventName;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.allDayStatus = allDayStatus;
	}

}
