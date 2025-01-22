package com.hana4.sonjumoney.dto;

import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Allowance;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateAllowanceThanksDto(
	Allowance allowance,
	@Nullable
	MultipartFile image,
	String message
) {
	public static CreateAllowanceThanksDto of(Allowance allowance, MultipartFile image, String message) {
		return CreateAllowanceThanksDto.builder()
			.allowance(allowance)
			.image(image)
			.message(message)
			.build();
	}

}
