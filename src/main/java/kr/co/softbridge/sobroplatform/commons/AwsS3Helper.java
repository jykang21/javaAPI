package kr.co.softbridge.sobroplatform.commons;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * AWS S3 Helper
 */
@Component
public class AwsS3Helper {

//    @Value("${cloud.aws.credentials.accessKey}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secretKey}")
//    private String secretKey;

    @Value("${cloud.aws.s3.vodBucket}")
    @Getter
    private String vodBucket;

    @Value("${cloud.aws.s3.attachBucket}")
    @Getter
    private String attachBucket;

    @Value("${cloud.aws.s3.recBucket}")
    @Getter
    private String recBucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Getter
    private AmazonS3 s3Client;

    @PostConstruct
    public void buildClient() {
//        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

//        s3Client = AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(region)
//                .build();
    }
}