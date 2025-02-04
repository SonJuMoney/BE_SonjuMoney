package com.hana4.sonjumoney.service;

import static com.hana4.sonjumoney.exception.ErrorCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.ContentPrefix;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.util.ContentUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.IoUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;


	@Value("${spring.cloud.aws.s3.bucket-name}")
	private String bucketName;

	@Value("${spring.cloud.aws.s3.cloudfront.baseurl}")
	private String baseUrl;

	public List<String> uploadImagesToS3(List<MultipartFile> images, ContentPrefix prefix, Long feedId) {
		List<String> urlList = new ArrayList<>();
		for (MultipartFile image : images) {
			String contentUrl = uploadImageToS3(image, prefix, feedId);
			urlList.add(contentUrl);
		}
		return urlList;
	}

	public String uploadImageToS3(MultipartFile image, ContentPrefix prefix, Long feedId) {
		String originalFilename = image.getOriginalFilename();
		assert originalFilename != null;
		String extension = ContentUtil.getExtension(originalFilename);
		String s3FileName = prefix.getPrefix() + feedId + "/" + createFileName(originalFilename);

		try (InputStream is = image.getInputStream();) {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(s3FileName)
				.contentType("image/" + extension)
				.build();

			byte[] bytes = IoUtils.toByteArray(is);
			RequestBody requestBody = RequestBody.fromBytes(bytes);
			s3Client.putObject(putObjectRequest, requestBody);
		} catch (IOException e) {
			throw new CommonException(S3_PROCESS_FAILED);
		}
		return getUrlFromKey(s3FileName);
	}

	public void deleteImage(String url) {
		String key = getKeyFromUrl(url);
		try {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
			s3Client.deleteObject(deleteObjectRequest);
		} catch (Exception e) {
			throw new CommonException(S3_PROCESS_FAILED);
		}
	}

	private String createFileName(String filename) {
		return createFileId() + "_" + filename;
	}
	private String createFileId() {
		return UUID.randomUUID().toString();
	}

	private String getKeyFromUrl(String url) {
		String domain = "https://" + baseUrl + "/";
		return url.substring(domain.length());
	}

	private String getUrlFromKey(String key) {
		return "https://" + baseUrl + "/" + key;
	}

}
