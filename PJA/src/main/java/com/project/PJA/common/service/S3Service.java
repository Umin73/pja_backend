package com.project.PJA.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file, String type, Long id) throws IOException {
        String originalName = file.getOriginalFilename();
        String uniqueFileName = type + id + "_" + UUID.randomUUID() + "_" + originalName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(uniqueFileName)
                .acl("public-read")
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + uniqueFileName;
    }

    public void deleteFile(String fileUrl) {
        String fileKey = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();
        s3Client.deleteObject(request);
    }
}
