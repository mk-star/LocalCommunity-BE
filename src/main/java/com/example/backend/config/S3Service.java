package com.example.backend.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3Service.class);
    private final AmazonS3 amazonS3; // AWS S3와 상호작용하기 위한 클라이언트

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(AmazonS3 amazonS3Client) {
        this.amazonS3 = amazonS3Client;
    }

    // 파일 업로드 요청
    // MultipartFile을 전달받아 File로 전환한 후(convert) S3에 업로드
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName);
    }

    //실제 업로드
    private String upload(File uploadFile, String dirName){
        String fileName = dirName + "/" + UUID.randomUUID();
        String uploadImageUrl = putS3(uploadFile, fileName); //변환된 파일을 S3에 업로드
        removeNewFile(uploadFile); // convert() 과정에서 로컬에 생성된 파일 삭제

        return uploadImageUrl; // url 반환
    }

    // 파일을 S3에 업로드, PublicRead 권한 설정
    private String putS3(File uploadFile, String fileName){
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        return amazonS3.getUrl(bucket, fileName).toString(); // File의 URL return
    }

    private void removeNewFile(File targetFile){
        String name = targetFile.getName();
        // convert() 과정에서 로컬에 생성된 파일을 삭제
        if (targetFile.delete()){
            log.info(name + "파일 삭제 완료");
        } else {
            log.info(name + "파일 삭제 실패");
        }
    }

    // 파일 변환 메서드 MultipartFile -> file
    private Optional<File> convert(MultipartFile file) throws  IOException {
        File convertFile = new File(file.getOriginalFilename()); // 업로드한 파일의 이름

        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public void deleteFile(String fileName) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
        amazonS3.deleteObject(request);
    }
}