package com.gallery_app.core_service.image.service;

import com.gallery_app.core_service.config.AwsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final S3Client s3Client;
    private final AwsConfig awsConfig;

    public String uploadMultipartFile(String key, MultipartFile file) {
        try {
            String bucketName = awsConfig.getBucketName();
            s3Client.createBucket(b -> b.bucket(bucketName));
            s3Client.createBucket(b -> b.bucket("thumbnail-bucket"));

            Map<String, String> metadata = new HashMap<>();
            metadata.put("original-filename", file.getOriginalFilename());
            metadata.put("content-type", file.getContentType());

            // Upload the file content to S3
            try (InputStream fileInputStream = file.getInputStream()) {
                PutObjectResponse response = s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .metadata(metadata)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(fileInputStream, file.getSize())
                );

                log.info("File uploaded successfully to key: {} with ETag: {}", key, response.eTag());
                return constructS3Url(bucketName, key);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private String constructS3Url(String bucketName, String key) {
        String endpoint = awsConfig.getEndpoint();
        return String.format("%s/%s/%s", endpoint, bucketName, key);
    }

}
