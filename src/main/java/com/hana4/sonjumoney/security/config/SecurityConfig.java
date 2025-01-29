package com.hana4.sonjumoney.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.hana4.sonjumoney.exception.ErrorCode;
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
	private final PasswordEncoder passwordEncoder;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/api/auth/error", "/api/auth/sign-up", "/api/auth/sign-in",
					"/api/auth/id-duplication",
					"/api/auth/resident-duplication", "/api/auth/phone-duplication",
					"/api/auth/reissue", "/api/videos/stream", "/ws/alarms", "/actuator/**")
				.permitAll()
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html").permitAll()
				.anyRequest()
				.authenticated()
			)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
					response.getWriter().write(ErrorCode.UNAUTHORIZED.getMessage());
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(ErrorCode.FORBIDDEN.getHttpStatus().value());
					response.getWriter().write(ErrorCode.FORBIDDEN.getMessage());
				})
			)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.addFilterBefore(new LoginFilter(authenticationManager(), jwtUtil),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, authService),
				UsernamePasswordAuthenticationFilter.class)
		;

		return http.build();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(authService);
		authProvider.setPasswordEncoder(passwordEncoder);
		authProvider.setHideUserNotFoundExceptions(false);
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// configuration.addAllowedOrigin("http://localhost:3000");
		// configuration.addAllowedOrigin("https://sonjumoney.topician.com");
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
