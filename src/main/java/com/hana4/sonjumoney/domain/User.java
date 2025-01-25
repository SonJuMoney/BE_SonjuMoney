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
import lombok.Setter;

@Entity
@Getter
@Table(name = "User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long id;

	@Column(name = "user_name", length = 20, nullable = false)
	private String username;

	@Column(name = "auth_id", length = 100, nullable = false, unique = true)
	private String authId;

	@Column(length = 100, nullable = false)
	private String password;

	@Column(length = 11)
	private String phone;

	@Column(name = "resident_num", length = 14, nullable = false)
	private String residentNum;

	@Column(length = 6, nullable = false)
	private String pin;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(name = "profile_link", length = 500)
	@Setter(AccessLevel.PUBLIC)
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
