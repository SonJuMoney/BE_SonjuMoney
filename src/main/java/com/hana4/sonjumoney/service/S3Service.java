package com.hana4.sonjumoney.service;

import static com.hana4.sonjumoney.exception.ErrorCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.IoUtils;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;


	@Value("${spring.cloud.aws.s3.bucket-name}")
	private String bucketName;

	public String upload(MultipartFile file) throws S3Exception {
		if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
			throw new S3Exception(NOT_FOUND_DATA.getMessage(), null);
		}
		return this.uploadImage(file);
	}

	private String uploadImage(MultipartFile image) {
		try{
			return this.uploadImageToS3(image);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String uploadImageToS3(MultipartFile image) throws IOException {
		String originalFilename = image.getOriginalFilename();
		if (originalFilename == null) {
			throw new IllegalArgumentException("파일이름은 null일 수 없습니다.");
		}
		String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
		String s3FileName = createFileName(originalFilename);

		InputStream is = image.getInputStream();
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(s3FileName)
			.contentType("image/" + extention)
			.build();
		byte[] bytes = IoUtils.toByteArray(is);
		RequestBody requestBody = RequestBody.fromBytes(bytes);
		s3Client.putObject(putObjectRequest, requestBody);
		return s3FileName;
	}

	private String createFileName(String filename) {
		return createFileId() + "_" + filename;
	}
	private String createFileId() {
		return UUID.randomUUID().toString();
	}

}
