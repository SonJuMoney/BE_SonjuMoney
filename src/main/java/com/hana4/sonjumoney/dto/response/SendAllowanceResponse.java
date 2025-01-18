package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record SendAllowanceResponse(
        String message
) {
    public static SendAllowanceResponse of(String message) {
        return SendAllowanceResponse.builder()
                .message(message)
                .build();
    }
}
