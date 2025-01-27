package com.hana4.sonjumoney.util;

public class RedisKeyUtil {
	private static final String SAVINGS_MESSAGE_KEY_PATTERN = "withdrawalAccountId:%d:autoTransferId:%d";

	/*적금 메세지 key 생성 메서드*/
	public static String createSavingsMessageKey(Long withdrawalAccountId, Long autoTransferId) {
		return String.format(SAVINGS_MESSAGE_KEY_PATTERN, withdrawalAccountId, autoTransferId);
	}
}
