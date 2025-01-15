package com.hana4.sonjumoney.security.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

	private final SecretKey secretKey;

	@Value("${jwt.token.access.expiration}")
	private Long accessTokenExpiration;

	@Value("${jwt.token.refresh.expiration}")
	private Long refreshTokenExpiration;

	public JwtUtil(@Value("${spring.security.jwt.key}") String secretKey) {
		this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		String authId = getAuthId(token);
		return authId.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public boolean validateRefreshToken(String refreshToken) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(refreshToken);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getAuthId(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("authId", String.class);
	}

	public Long getUserId(String token) {
		String userId = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("userId", String.class);
		return Long.parseLong(userId);
	}

	public Boolean isTokenExpired(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	public String generateToken(String authId, Long userId, Long expired) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expired);

		return Jwts.builder()
			.claim("authId", authId)
			.claim("userId", String.valueOf(userId))
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey)
			.compact();
	}

	public String generateAccessToken(String authId, Long userId) {
		return generateToken(authId, userId, accessTokenExpiration);
	}

	public String generateRefreshToken(String authId, Long userId) {
		return generateToken(authId, userId, refreshTokenExpiration);
	}

	public String refreshAccessToken(String refreshToken) {
		String authId = getAuthId(refreshToken);
		Long userId = getUserId(refreshToken);
		return generateAccessToken(authId, userId);
	}
}
