package kr.co.softbridge.sobroplatform.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Security;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jets3t.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cloudfront.util.SignerUtils.Protocol;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.util.DateUtils;
import org.jets3t.service.CloudFrontService;

@Component
public class AwsCloudFrontURL {
	private static final Logger logger = LogManager.getLogger(AwsCloudFrontURL.class);

    @Value("${cloud.aws.cloudFront.keyPairId}")
    private String keyPairId;
    
    @Value("${cloud.aws.cloudFront.privateKey}")
    private String privateKey;
    
	public String getUrl(String s3ObjectKey, String distributionDomain, String type) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		String returnString = null;
		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	Date act = new Date(); // 생성 시간
    	Date dateLessThan = DateUtils.parseISO8601Date(simpleDateFormat.format(act));
    	Date dateGreaterThan = DateUtils.parseISO8601Date(simpleDateFormat.format(act.getTime() + (1000 * 60L * 60L * 24L * 30L))); // 유효 시간 (30일)
    	Protocol protocol = Protocol.https;
    	
    	try {	
			if ("canned".equals(type)) {
				/* returnString = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
			              protocol, distributionDomain, new File(privateKey),
			              s3ObjectKey, keyPairId, dateLessThan);*/
				ClassPathResource classPathResource = new ClassPathResource(privateKey);
				byte[] derPrivateKey = ServiceUtils.readInputStreamToBytes(classPathResource.getInputStream());
		        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				String policyResourcePath = "https://" + distributionDomain + "/" + s3ObjectKey;
				returnString = CloudFrontService.signUrlCanned(
		                policyResourcePath,
		                keyPairId,
		                derPrivateKey,
		                dateGreaterThan
		        );
			} else if ("policy".equals(type)) {				
				returnString = CloudFrontUrlSigner.getSignedURLWithCustomPolicy(
			              protocol, distributionDomain, new File(privateKey),
			              s3ObjectKey, keyPairId, dateLessThan,
			              dateGreaterThan, "0.0.0.0/0");
	
			}
    	} catch(Exception e) {
    		logger.info("ERROR_MSG=" + e);
    	}

		return returnString;
	}
}
