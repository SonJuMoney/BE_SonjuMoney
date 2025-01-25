package com.hana4.sonjumoney.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		/*key를 문자열로 직렬화*/
		template.setKeySerializer(new StringRedisSerializer());
		/*value는 json으로 직렬화*/
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		return template;
	}
}
