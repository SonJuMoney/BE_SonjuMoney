package com.hana4.sonjumoney.exception;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hana4.sonjumoney.dto.global.ExceptionDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// Custom Exception
	@ExceptionHandler(CommonException.class)
	@ResponseBody
	public ResponseEntity<ExceptionDto> commonException(CommonException e) {
		log.error(e.getMessage(), " ", e.getStackTrace()[0].getMethodName(), e);
		return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ExceptionDto.of(e.getErrorCode()));
	}

	/*redis 연결 예외처리*/
	@ExceptionHandler(RedisConnectionFailureException.class)
	@ResponseBody
	public ResponseEntity<ExceptionDto> redisConnectionException(RedisConnectionFailureException e) {
		log.error(e.getMessage());
		return ResponseEntity
			.status(ErrorCode.REDIS_CONNECTION_FAILED.getHttpStatus())
			.body(ExceptionDto.of(ErrorCode.REDIS_CONNECTION_FAILED));
	}

	//  Server Exception
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<?> exception(Exception e) {
		log.info(e.getMessage());
		return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
			.body(ExceptionDto.of(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
