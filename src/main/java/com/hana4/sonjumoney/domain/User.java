package com.hana4.sonjumoney.domain;

import com.hana4.sonjumoney.domain.enums.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long id;

	@Column(name = "user_name", nullable = false)
	private String username;

	@Column(name = "auth_id", nullable = false)
	private String authId;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String phone;

	@Column(name = "resident_num", nullable = false)
	private String residentNum;

	@Column(nullable = false)
	private String pin;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false)
	private String profileLink;

	@Builder
	public User(String username, String authId, String password, String phone, String residentNum, String pin,
		Gender gender, String profileLink) {
		this.username = username;
		this.authId = authId;
		this.password = password;
		this.phone = phone;
		this.residentNum = residentNum;
		this.pin = pin;
		this.gender = gender;
		this.profileLink = profileLink;
	}
}
