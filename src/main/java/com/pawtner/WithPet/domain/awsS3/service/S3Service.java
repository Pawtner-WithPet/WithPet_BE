package com.pawtner.WithPet.domain.awsS3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pawtner.WithPet.domain.awsS3.exception.S3OperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드: S3에 파일 업로드 후 URL 반환
    public String uploadFile(MultipartFile file, String folderName) {
        String fileName = folderName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new S3OperationException("S3 업로드 중 오류 발생", e);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    // 파일 삭제: 기존 S3 URL 이용
    public void deleteFileByUrl(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            String key = uri.getPath().substring(1);
            amazonS3.deleteObject(bucket, key);
        } catch (URISyntaxException e) {
            throw new S3OperationException("잘못된 S3 URL입니다.", e);
        }
    }
}
