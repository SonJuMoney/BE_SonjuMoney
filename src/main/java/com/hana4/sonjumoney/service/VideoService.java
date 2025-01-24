package com.hana4.sonjumoney.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.http.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.ContentExtension;
import com.hana4.sonjumoney.dto.ContentPrefix;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.util.ContentUtil;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
	private static final long MAX_FILE_SIZE = 1024L * 1024L * 1024L * 5L;

	public List<String> uploadVideos(List<MultipartFile> videos, ContentPrefix prefix, Long feedId) {
		List<String> contentList = new ArrayList<>();
		for (MultipartFile video : videos) {
			contentList.add(uploadVideo(video, prefix, feedId));
		}
		return contentList;
	}

	public String uploadVideo(MultipartFile video, ContentPrefix prefix, Long feedId) {
		String originalFileName = video.getOriginalFilename();
		if (originalFileName == null) {
			throw new CommonException(ErrorCode.WRONG_FILE_NAME);
		}
		checkFileSize(video);
		String extension = ContentUtil.getExtension(originalFileName);
		checkFileType(extension);

		String fileName = UUID.randomUUID() + extension;
		String directoryPath = getDirectoryPath(prefix, feedId);
		Path filePath = Paths.get(directoryPath, fileName);
		try (OutputStream os = Files.newOutputStream(filePath)) {
			os.write(video.getBytes());
			log.info(filePath.toString());
			return filePath.toString();
		} catch (IOException e) {
			log.error("file upload failed", e);
			throw new CommonException(ErrorCode.VIDEO_UPLOAD_FAILED);
		}
	}

	public ResponseEntity<ResourceRegion> streamingVideo(HttpHeaders httpHeaders, String pathStr) {
		try {
			Path path = Paths.get(pathStr);
			Resource resource = new FileSystemResource(path);
			long chunkSize = 1024 * 1024;
			long contentLength = resource.contentLength();
			Assert.isTrue(contentLength > 0, "리소스 컨텐츠 길이는 0보다 커야합니다.");
			ResourceRegion resourceRegion;
			try {
				HttpRange httpRange;
				if (httpHeaders.getRange().stream().findFirst().isPresent()) {
					httpRange = httpHeaders.getRange().stream().findFirst().get();
					long start = httpRange.getRangeStart(contentLength);
					long end = httpRange.getRangeEnd(contentLength);
					long rangeLength = Long.min(chunkSize, end - start + 1);

					resourceRegion = new ResourceRegion(resource, start, rangeLength);
				} else {
					resourceRegion = new ResourceRegion(resource, 0, Long.min(chunkSize, resource.contentLength()));
				}
			} catch (Exception e) {
				long rangeLength = Long.min(chunkSize, resource.contentLength());
				resourceRegion = new ResourceRegion(resource, 0, rangeLength);
			}
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
				.cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
				.contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
				.header(HttpHeaders.ACCEPT_RANGES,"bytes")
				.body(resourceRegion);
		} catch (IOException e) {
			throw new IllegalArgumentException("리소스의 컨텐츠 링크를 가져오지 못했습니다.", e);
		}
	}


	private String getDirectoryPath(ContentPrefix prefix,Long feedId) {
		String directory = "src/main/resources/static/video/" + prefix.getPrefix() + feedId;
		Path path = Paths.get(directory);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				throw new CommonException(ErrorCode.VIDEO_UPLOAD_FAILED);
			}
		}
		return path.toString();
	}

	private void checkFileSize(MultipartFile video) {
		if (video.getSize() > MAX_FILE_SIZE) {
			throw new CommonException(ErrorCode.EXCESSIVE_SIZE);
		}
	}

	private void checkFileType(String extension) {
		if (!extension.equals(ContentExtension.MP4.getUploadExtension())) {
			throw new CommonException(ErrorCode.WRONG_FILE_TYPE);
		}
	}
}
