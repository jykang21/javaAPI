package kr.co.softbridge.sobroplatform.file.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import kr.co.softbridge.sobroplatform.commons.AwsCloudFrontURL;
import kr.co.softbridge.sobroplatform.commons.FileAse256;
import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.RoomVerifyResponseDto;
import kr.co.softbridge.sobroplatform.commons.exception.ApiException;
import kr.co.softbridge.sobroplatform.commons.service.CommonTokenService;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.file.dto.FileDeleteResponseDto;
import kr.co.softbridge.sobroplatform.file.dto.FileDownloadRequestDto;
import kr.co.softbridge.sobroplatform.file.dto.FileManagerDto;
import kr.co.softbridge.sobroplatform.file.mapper.FileMapper;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * 파일 다운로드 service
 */
@Service
public class FileDownloadService {
	
	private static final Logger logger = LogManager.getLogger(FileDownloadService.class);
	
    @Value("${platform.aes256.key}")
    private String key;
    
    @Value("${cloud.aws.cloudFront.fileDomain}")
    private String fileDomain;
    
    @Value("${cloud.aws.cloudFront.vodDomain}")
    private String vodDomain;
    
    @Value("${cloud.aws.cloudFront.recDomain}")
    private String recDomain;

    @Value("${cloud.aws.s3.recBucket}")
    @Getter
    private String recBucket;
    
    @Value("${cloud.aws.region.static}")
    private String region;	/*** 클라이언트 지역 ***/

    @Value("${cloud.aws.s3.vodBucket}")
    @Getter
    private String vodBucket;

    @Value("${cloud.aws.s3.attachBucket}")
    @Getter
    private String attachBucket;
    
    @Value("${temp.path}")
    private String tmpPath;
    
    @Autowired
    private CommonTokenService commonTokenService;
    
    @Autowired
    private AwsCloudFrontURL awsCloudFrontURL;
    
    @Autowired
    private FileMapper fileMapper;

    /**
     * 파일 다운로드용 스트림 생성 반환 처리
     * @param <Boolen>
     * @param fileDownloadRequestDto.
     * @return
     * @throws Exception 
     */
    public <Boolen> ResponseEntity<byte[]> downloadFile(HttpServletRequest request, @Valid FileDownloadRequestDto fileDownloadRequestDto) throws Exception {
    	
    	byte[] bytes = new byte[] {};
    	HttpHeaders httpHeaders = new HttpHeaders();
    	InputStream is = null;
    	HttpsURLConnection connection = null;
    	
    	try {
    		logger.info("[downloadFile] " + fileDownloadRequestDto.toStringShortPrefix());
    		if (
    				StringUtil.isEmpty(fileDownloadRequestDto.getFilePath()) 
    				|| StringUtil.isEmpty(fileDownloadRequestDto.getRoomCode()) 
    				|| StringUtil.isEmpty(fileDownloadRequestDto.getRoomToken())
    				|| StringUtil.isEmpty(fileDownloadRequestDto.getUserId())
    				|| StringUtil.isEmpty(fileDownloadRequestDto.getUserNm())
    				|| (fileDownloadRequestDto.getFileSeq() == 0)
    		) {
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
        	
        	HashMap<String, Object> dbMap = new HashMap<String, Object>();
        	dbMap.clear();
        	dbMap.put("roomToken", fileDownloadRequestDto.getRoomToken());
        	dbMap.put("roomCode", fileDownloadRequestDto.getRoomCode());
        	dbMap.put("userId", fileDownloadRequestDto.getUserId());
        	dbMap.put("userNm", fileDownloadRequestDto.getUserNm());
        	
        	RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
        	if("000000".equals(roomTokenInfo.getResultCode())) {
        		/* 통합 로그 남기기 위해 set */
        		fileDownloadRequestDto.setSvcCode(roomTokenInfo.getSvcCode());
        		
        		/* 파일의 정보를 불러온다 */
        		FileManagerDto fileManagerDto = FileManagerDto.builder()
                        .roomCode(fileDownloadRequestDto.getRoomCode())
                        .fileSeq(fileDownloadRequestDto.getFileSeq())
                        .build();
        		
        		FileDeleteResponseDto fileResopnse = fileMapper.getFileByIdToDeleteFile(fileManagerDto);
        		/* 파일의 정보를 불러온다 */
        		
        		// AmazonS3 s3Client = awsS3Helper.getS3Client();
        		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withRegion(region)
                        .withCredentials(new ProfileCredentialsProvider())
                        .build();
    	        String bucket		= attachBucket;
    	        /* 임시 세팅 */
    	        fileDownloadRequestDto.setFileType(fileResopnse.getFileType()); // 01: 일반첨부, 02: 동영상
    	        fileDownloadRequestDto.setVodType(fileResopnse.getVodType());	// vod : 일반동영상, rec : 녹화동영상
    	        /* 임시 세팅 */
    	        // String fileType 	= "03";	// rec
    	        String domwin		= fileDomain;
        		if ("02".equals(fileDownloadRequestDto.getFileType())) {
        			if("vod".equals(fileDownloadRequestDto.getVodType())) {
        	        	bucket = vodBucket;
        	        	domwin = vodDomain+"/vod";
        			} else if("rec".equals(fileDownloadRequestDto.getVodType())){
        	        	bucket = recBucket;
        	        	domwin = recDomain+"/rec";
        			}
        		}
    	
        		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
                FileAse256 fileEnc = new FileAse256(secretKey);
    	        String filePath = awsCloudFrontURL.getUrl(fileDownloadRequestDto.getFilePath(), domwin, "canned");

    	        URL url = new URL(filePath.replaceAll(" ", "%20"));
    	        connection = (HttpsURLConnection) url.openConnection();
    	        is = connection.getInputStream();
 	        
	        	ByteArrayOutputStream decObject = new ByteArrayOutputStream();
    	        fileEnc.decrypt(is, decObject);
    	        bytes = decObject.toByteArray();
    	        String fileName = URLEncoder.encode(fileDownloadRequestDto.getFilePath(), "UTF-8").replaceAll("\\+", "%20");
    	        
    	        String[] fileNameArray = fileDownloadRequestDto.getFilePath().split("/");
    	        if(fileNameArray.length > 0) {
    	        	fileName = fileNameArray[fileNameArray.length-1];
    	        }
    			
    	        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    	        httpHeaders.setContentLength(bytes.length);
    	        httpHeaders.setContentDispositionFormData("attachment", new String(fileResopnse.getFileNm().getBytes("UTF-8"), "ISO-8859-1"));
    	
        	}else {
        		throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
        	}
    	
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[downloadFile] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[downloadFile] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		return new ResponseEntity<byte[]>((HttpStatus.BAD_REQUEST));
    	}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<byte[]>((HttpStatus.BAD_REQUEST));
		}finally {
			if(connection != null) {
				try {
					connection.disconnect();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(is != null) {
				try {
					is.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
    	return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }
}
