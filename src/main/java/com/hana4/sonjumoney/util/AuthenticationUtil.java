package com.hana4.sonjumoney.util;

import org.springframework.security.core.Authentication;

import com.hana4.sonjumoney.security.model.CustomUserDetails;

public class AuthenticationUtil {
	public static Long getUserId(Authentication authentication) {
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
		return customUserDetails.getUserId();
	}
}
