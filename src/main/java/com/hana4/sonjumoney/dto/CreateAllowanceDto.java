package com.hana4.sonjumoney.dto;

import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Allowance;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateAllowanceDto(
	Allowance allowance,
	@Nullable
	MultipartFile image,
	String message
) {
	public static CreateAllowanceDto of(Allowance allowance, MultipartFile image, String message) {
		return CreateAllowanceDto.builder()
			.allowance(allowance)
			.image(image)
			.message(message)
			.build();
	}

}
