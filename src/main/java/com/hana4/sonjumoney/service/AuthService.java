package com.hana4.sonjumoney.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.security.model.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String authId) throws UsernameNotFoundException {
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new CustomUserDetails(user.getId(), user.getAuthId(), user.getPassword(), Collections.emptyList());
	}
}
