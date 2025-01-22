package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SendAllowanceResponse(
    Integer code,
    String message,
    @JsonProperty("allowance_id")
    Long allowanceId
) {
    public static SendAllowanceResponse of(Integer code, String message, Long allowanceId) {
        return SendAllowanceResponse.builder()
            .code(code)
            .message(message)
            .allowanceId(allowanceId)
            .build();
    }
}
