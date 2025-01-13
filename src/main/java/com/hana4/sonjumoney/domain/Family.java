package com.hana4.sonjumoney.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Family")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Family extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "family_id", nullable = false)
	private Long id;

	@Column(name = "family_name", length = 20, nullable = false)
	private String familyName;

	@Builder
	public Family(String familyName) {
		this.familyName = familyName;
	}
}
