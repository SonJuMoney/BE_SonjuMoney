// package com.hana4.sonjumoney.security.filter;
//
// import java.io.IOException;
//
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
// import com.hana4.sonjumoney.security.util.JwtUtil;
//
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// public class LoginFilter extends UsernamePasswordAuthenticationFilter {
//
// 	private final AuthenticationManager authenticationManager;
// 	private final JwtUtil jwtUtil;
//
// 	@Override
// 	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
// 		Authentication authResult) throws IOException, ServletException {
// 		String authId = authResult.getDetails().toString();
// 	}
// }
