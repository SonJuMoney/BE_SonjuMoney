package com.hana4.sonjumoney.service;

import static com.hana4.sonjumoney.exception.ErrorCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// import com.hana4.sonjumoney.dto.ContentExtension;
// import com.hana4.sonjumoney.dto.PresignedUrlDto;

import com.hana4.sonjumoney.dto.ImagePrefix;
import com.hana4.sonjumoney.util.ContentUtil;

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

	@Value("${spring.cloud.aws.s3.cloudfront.baseurl}")
	private String baseUrl;

	// public PresignedUrlDto getPresignedUrlforAllowance(ContentExtension contentExtension) {
	// 	String extension = contentExtension.getUploadExtension();
	// 	String
	// }

	public String uploadImageToS3(MultipartFile image, ImagePrefix prefix) throws IOException {
		String originalFilename = image.getOriginalFilename();
		if (originalFilename == null) {
			throw new IllegalArgumentException("파일이름은 null 일 수 없습니다.");
		}
		String extension = ContentUtil.getExtension(originalFilename);
		String s3FileName = prefix.getPrefix() + createFileName(originalFilename);

		InputStream is = image.getInputStream();
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(s3FileName)
			.contentType("image/" + extension)
			.build();

		byte[] bytes = IoUtils.toByteArray(is);
		RequestBody requestBody = RequestBody.fromBytes(bytes);
		s3Client.putObject(putObjectRequest, requestBody);
		return "https://" + baseUrl + s3FileName;
	}

	private String createFileName(String filename) {
		return createFileId() + "_" + filename;
	}
	private String createFileId() {
		return UUID.randomUUID().toString();
	}

}
