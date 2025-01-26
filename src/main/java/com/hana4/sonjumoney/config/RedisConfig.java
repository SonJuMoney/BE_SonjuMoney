package com.hana4.sonjumoney.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory connectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		/*key를 문자열로 직렬화*/
		template.setKeySerializer(new StringRedisSerializer());

		/*value도 문자열로 직렬화*/
		template.setValueSerializer(new StringRedisSerializer());

		return template;
	}

}
