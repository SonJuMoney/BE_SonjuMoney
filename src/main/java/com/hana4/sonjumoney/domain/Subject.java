package com.hana4.sonjumoney.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Subject")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subject_id", nullable = false)
	private Long id;

	@Column(length = 100, nullable = false)
	private String subject;

	@Column(name = "age_range1", nullable = false)
	private Integer ageRange1;

	@Column(name = "age_range2", nullable = false)
	private Integer ageRange2;

	@Builder
	public Subject(String subject, Integer ageRange1, Integer ageRange2) {
		this.subject = subject;
		this.ageRange1 = ageRange1;
		this.ageRange2 = ageRange2;
	}

}
