package com.hana4.sonjumoney.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hana4.sonjumoney.security.filter.JwtAuthenticationFilter;
import com.hana4.sonjumoney.security.filter.LoginFilter;
import com.hana4.sonjumoney.security.util.JwtUtil;
import com.hana4.sonjumoney.service.AuthService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final AuthService authService;
	private final AuthenticationConfiguration authenticationConfiguration;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/api/auth/sign-up", "/api/auth/sign-in", "/api/auth/id-duplication",
					"/api/auth/reissue")
				.permitAll()
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html").permitAll()
				.anyRequest()
				.authenticated()
			)
			.addFilter(new LoginFilter(authenticationManager(), jwtUtil))
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, authService),
				UsernamePasswordAuthenticationFilter.class)
		;

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
