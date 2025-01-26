package com.hana4.sonjumoney.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.util.RedisKeyUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
	private final RedisTemplate<String, String> redisTemplate;

	/*적금 메세지 생성*/
	public String createSavingsMessage(Long withdrawalAccountId, Long autoTransferId, String message) {
		String key = RedisKeyUtil.createSavingsMessageKey(withdrawalAccountId, autoTransferId);
		try {
			redisTemplate.opsForValue().set(key, message);
		} catch (Exception e) {
			throw new CommonException(ErrorCode.REDIS_OPERATION_FAILED);
		}
		return message;
	}
}
