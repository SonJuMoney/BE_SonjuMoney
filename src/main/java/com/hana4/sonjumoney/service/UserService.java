package com.hana4.sonjumoney.service;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
}
