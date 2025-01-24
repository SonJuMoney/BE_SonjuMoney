package com.hana4.sonjumoney.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {
	@Value("${spring.cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Bean
	public S3Client s3Client() {
		AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return S3Client.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.build();
	}
}
