package com.project.chawchaw.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.user.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final UserRepository userRepository;

    public static final String CLOUD_FRONT_DOMAIN_NAME = "d3t4l8y7wi01lo.cloudfront.net";

    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${file.defaultImage}")
    private String defaultImage;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    @Transactional
    public String profileImageUpload(MultipartFile file, Long id) throws IOException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        String fileName = user.getImageUrl().split("net/")[1];
        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String newFileName = date.format(new Date()) + "_" + file.getOriginalFilename();


        if (!defaultImage.equals(fileName) && fileName != null) {
            boolean isExistObject = s3Client.doesObjectExist(bucket, fileName);

            if (isExistObject == true) {
                s3Client.deleteObject(bucket, fileName);
            }
        }


        s3Client.putObject(new PutObjectRequest(bucket, newFileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        String imageUrl = "https://" + CLOUD_FRONT_DOMAIN_NAME + "/" + newFileName;

        user.changeImageUrl(imageUrl);
        return imageUrl;
    }

    @Transactional
    public String chatImageUpload(MultipartFile file) throws IOException {

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String newFileName = date.format(new Date()) + "_" + file.getOriginalFilename();

        s3Client.putObject(new PutObjectRequest(bucket, newFileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        String imageUrl = "https://" + CLOUD_FRONT_DOMAIN_NAME + "/" + newFileName;
        return imageUrl;
    }


    //이미지 삭제
    @Transactional
    public Boolean deleteImage(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        String fileName = user.getImageUrl();


        if (!defaultImage.equals(fileName) && fileName != null) {
            boolean isExistObject = s3Client.doesObjectExist(bucket, fileName);

            if (isExistObject == true) {
                s3Client.deleteObject(bucket, fileName);
                user.changeImageUrl("https://" + CLOUD_FRONT_DOMAIN_NAME + "/" + defaultImage);
                return true;
            } else
                return false;
        } else {
            return false;
        }


    }

    //채팅방 삭제시 관련 사진 삭제
    @Transactional
    public void deleteChatImage(List<String> fileNameList) {


        for (String fileName : fileNameList) {
            boolean isExistObject = s3Client.doesObjectExist(bucket, fileName);

            if (isExistObject == true) {
                s3Client.deleteObject(bucket, fileName);

            }


        }
    }
}