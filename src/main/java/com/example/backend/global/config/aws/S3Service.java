package com.example.backend.global.config.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.backend.global.util.s3.exception.S3ErrorCode;
import com.example.backend.global.util.s3.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Config s3Config;
    private final AmazonS3 amazonS3;

    public List<String> uploadFile(List<MultipartFile> fileList){
        List<String> imageUrls = new ArrayList<>();

        for(MultipartFile file : fileList) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            try {
                String keyName = generatePostKeyName(UUID.randomUUID().toString());
                amazonS3.putObject(new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), metadata));
                imageUrls.add(amazonS3.getUrl(s3Config.getBucket(), keyName).toString());
            } catch (IOException e) {
                throw new S3Exception(S3ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }
        return imageUrls;
    }

    public String generatePostKeyName(String uuid) {
        return s3Config.getPostPath() + '/' + uuid;
    }

    public void deleteFile(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String key = url.getPath().substring(1);
            amazonS3.deleteObject(new DeleteObjectRequest(s3Config.getBucket(), key));
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.IMAGE_DELETE_FAILED);
        }
    }
}