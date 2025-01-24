package com.hana4.sonjumoney.security.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hana4.sonjumoney.domain.enums.Gender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final Long userId;
	private final String authId;
	private final String password;
	private final String name;
	private final String userProfile;
	private final Gender gender;
	private final String birthday;

	private final Collection<? extends GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return authId;
	}
}
