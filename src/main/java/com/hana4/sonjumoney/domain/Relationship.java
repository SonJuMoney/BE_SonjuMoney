package com.hana4.sonjumoney.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "Relationship")
public class Relationship {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long relationshipId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User parentId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User childId;
}
