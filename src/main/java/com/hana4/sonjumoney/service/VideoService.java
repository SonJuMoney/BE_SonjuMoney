package com.hana4.sonjumoney.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hana4.sonjumoney.dto.ContentPrefix;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.util.ContentUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
	private static final long MAX_FILE_SIZE = 1024L * 1024L * 1024L * 10L;

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
			return filePath.toString();
		} catch (IOException e) {
			log.error("file upload failed", e);
			throw new CommonException(ErrorCode.VIDEO_UPLOAD_FAILED);
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
		if (!extension.equals(".mp4")) {
			throw new CommonException(ErrorCode.WRONG_FILE_TYPE);
		}
	}
}
