package com.hana4.sonjumoney.domain;

import java.time.LocalDate;

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

	@Column(name = "start_date", nullable = false, columnDefinition = "DATE")
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false, columnDefinition = "DATE")
	private LocalDate endDate;

	@Column(name = "event_category", nullable = false)
	@Enumerated(EnumType.STRING)
	private EventCategory eventCategory;

	@Column(name = "notify_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotifyStatus notifyStatus;

	@Builder
	public Event(Family family, String eventName, LocalDate startDate, LocalDate endDate, EventCategory eventCategory,
		NotifyStatus status) {
		this.family = family;
		this.eventName = eventName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.eventCategory = eventCategory;
		this.notifyStatus = status;
	}

	public void updateEvent(EventCategory eventCategory, String eventName, LocalDate startDate, LocalDate endDate) {
		this.eventCategory = eventCategory;
		this.eventName = eventName;
		this.startDate = startDate;
		this.endDate = endDate;
	}

}
