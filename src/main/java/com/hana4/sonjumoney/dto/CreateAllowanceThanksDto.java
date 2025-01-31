package com.hana4.sonjumoney.dto;

import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.domain.Allowance;
import com.hana4.sonjumoney.domain.enums.FeedType;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateAllowanceThanksDto(
	Allowance allowance,
	@Nullable
	MultipartFile file,
	String message,
	FeedType feedType
) {
	public static CreateAllowanceThanksDto of(Allowance allowance, MultipartFile file, String message, FeedType feedType) {
		return CreateAllowanceThanksDto.builder()
			.allowance(allowance)
			.file(file)
			.message(message)
			.feedType(feedType)
			.build();
	}

}
