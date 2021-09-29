package kr.co.softbridge.sobroplatform.commons;
import com.amazonaws.auth.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;

@Component
public class MakingRequestsWithIAMTempCredentials {

    @Value("${cloud.aws.region.static}")
    private String region;	/*** 클라이언트 지역 ***/

//    @Value("${cloud.aws.credentials.roleSessionName}")
//    private String roleSessionName;	/*** 역할 세션 이름 ***/
//
//    /*** 수임할 역할에 대한 ARN ***/
//    @Value("${cloud.aws.credentials.roleArn}")
//    private String roleARN;
    
    /*** Profile명 ***/
    @Value("${cloud.aws.credentials.profileName}")
    private String profileName;
    
//    /*** Profile 경로 ***/
//    @Value("${cloud.aws.credentials.profilePath}")
//    private String profilePath;


    /**
     * <pre>
     * @Method Name : stsWithIAMTempCredentials
     * 1. 개요 : AWS IAM 임시권한취득
     * 2. 처리내용 : 
     * 3. 작성자	: 2021. 7. 29.
     * 4. 작성일	: sb.jykang
     * </pre>
     * 
     * @Parameter 
     * 		bucketName : 버킷 이름
     * @ReturnType	: 
     */
    public AmazonS3 stsWithIAMTempCredentials(String bucketName) {
    	AmazonS3 s3Client =  null;
        try {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSCredentialsProviderChain(
                            InstanceProfileCredentialsProvider.getInstance(),
                            new ProfileCredentialsProvider(profileName)
                    ))
                    .withRegion(region)
                    .build();

        	// STS 클라이언트 생성은 신뢰할 수 있는 코드의 일부입니다.
    		// 임시 보안 자격 증명을 얻는 데 사용하는 보안 자격 증명입니다.

//        	AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
//                    .withCredentials(new ProfileCredentialsProvider(profilePath, profileName))
//                    .withRegion(region)
//                    .build();

            // IAM 역할에 대한 자격 증명을 얻습니다. AWS 루트 계정의 역할은 맡을 수 없습니다.
            // Amazon S3는 액세스를 거부합니다. IAM 사용자 또는 IAM 역할에 대한 자격 증명을 사용해야 합니다.
//            AssumeRoleRequest roleRequest = new AssumeRoleRequest()
//                                                    .withRoleArn(roleARN)
//                                                    .withRoleSessionName(roleSessionName);
//            AssumeRoleResult roleResponse = stsClient.assumeRole(roleRequest);
//            Credentials sessionCredentials = roleResponse.getCredentials();
//
//            // 방금 검색한 자격 증명이 포함된 BasicSessionCredentials 개체를 만듭니다.
//            BasicSessionCredentials awsCredentials = new BasicSessionCredentials(
//                    sessionCredentials.getAccessKeyId(),
//                    sessionCredentials.getSecretAccessKey(),
//                    sessionCredentials.getSessionToken());

            /* 
             * Amazon S3 클라이언트가 인증된 요청을 Amazon S3로 보낼 수 있습니다.
             * 클라이언트를 생성 sessionCredentials 객체를 사용합니다.
             */
//            s3Client = AmazonS3ClientBuilder.standard()
//                                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
//                                    .withRegion(region)
//                                    .build();
        }
        catch(AmazonServiceException e) {
            // 호출이 성공적으로 전송되었지만 Amazon S3가 처리할 수 없음
            // 오류 응답 반환
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // 응답을 위해 Amazon S3에 연결할 수 없거나 클라이언트
            // Amazon S3의 응답을 구문 분석할 수 없습니다.
            e.printStackTrace();
        }
        return s3Client;
    }
}