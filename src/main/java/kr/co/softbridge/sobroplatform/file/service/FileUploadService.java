package kr.co.softbridge.sobroplatform.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import kr.co.softbridge.sobroplatform.commons.AwsCloudFrontURL;
import kr.co.softbridge.sobroplatform.commons.AwsS3Helper;
import kr.co.softbridge.sobroplatform.commons.FileAse256;
import kr.co.softbridge.sobroplatform.commons.FormatUtils;
import kr.co.softbridge.sobroplatform.commons.MakingRequestsWithIAMTempCredentials;
import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.RoomVerifyResponseDto;
import kr.co.softbridge.sobroplatform.commons.exception.ApiException;
import kr.co.softbridge.sobroplatform.commons.service.CommonTokenService;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadRequestDto;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadResponseDto;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadWhiteboardRequestDto;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadWhiteboardResponseDto;
import kr.co.softbridge.sobroplatform.file.mapper.FileMapper;
import lombok.Getter;
import kr.co.softbridge.sobroplatform.commons.util.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jets3t.service.utils.Mimetypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Service
public class FileUploadService {
	
	private static final Logger logger = LogManager.getLogger(FileUploadService.class);

    @Value("${platform.aes256.key}")
    private String key; 
    
    @Value("${temp.path}")
    private String tmpPath;
    
    @Value("${spring.servlet.multipart.max-file-size}")
    private long maxFileSize;
    
    @Autowired
    private FileMapper fileMapper;

    @Value("${cloud.aws.s3.vodBucket}")
    @Getter
    private String vodBucket;

    @Value("${cloud.aws.s3.recBucket}")
    @Getter
    private String recBucket;

    @Value("${cloud.aws.s3.attachBucket}")
    @Getter
    private String attachBucket;
    
    @Value("${cloud.aws.cloudFront.fileDomain}")
    private String fileDomain;
    
    @Value("${cloud.aws.cloudFront.vodDomain}")
    private String vodDomain;
    
    @Value("${cloud.aws.cloudFront.recDomain}")
    private String recDomain;
    
    @Value("${cloud.aws.whiteboard.filePath}")
    private String whiteboardFilePath;
    
    @Autowired
    private AwsCloudFrontURL awsCloudFrontURL;

    @Autowired
    private AwsS3Helper awsS3Helper;
    
    @Autowired
    private MakingRequestsWithIAMTempCredentials makingRequestsWithIAMTempCredentials;

	@Autowired
    private CommonTokenService commonTokenService;
	
    @Transactional
    public ResponseEntity<FileUploadResponseDto> uploadFile(HttpServletRequest request, FileUploadRequestDto fileUploadRequestDto) throws IOException {
    	HashMap<String, Object> tokenMap = new HashMap<String, Object>();
    	FileUploadRequestDto fileManager = new FileUploadRequestDto();
    	
    	try {
    		
    		logger.info("[uploadFile] " + fileUploadRequestDto.toStringShortPrefix());
    		
    		tokenMap.clear();
    		tokenMap.put("roomToken", fileUploadRequestDto.getRoomToken());
    		tokenMap.put("userId", fileUploadRequestDto.getUserId());
    		tokenMap.put("userNm", fileUploadRequestDto.getUserNm());
    		tokenMap.put("roomCode", fileUploadRequestDto.getRoomCode());
    		RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, tokenMap);
    		if("000000".equals(roomTokenInfo.getResultCode())) {
    			
    			fileUploadRequestDto.setSvcCode(roomTokenInfo.getSvcCode());
		    	fileManager = generateFileManager(fileUploadRequestDto);
		        MultipartFile file = fileUploadRequestDto.getFile();
		        
		        if(Long.parseLong(fileManager.getFileSize()) > Long.parseLong(convertFileSizeToEntityForm(maxFileSize))) {
		        	return null;
		        }
		        String file_url = uploadToStorage(file, fileManager);
		        fileManager.setFileUrl(file_url);
		        
		        // 파일기본정보 등록
		        fileMapper.saveFileMaster(fileManager);
		        int fileSeq = fileManager.getFILE_SEQ();
		        if(fileSeq > 0) {
			        fileManager.setFileSeq(fileSeq);
			        fileMapper.saveFileManager(fileManager);
			        
			        if("R0".equals(fileManager.getSvcTarget())) {
			        	if("02".equals(fileManager.getShareType())) {
			        		fileMapper.saveFileShareTarget(fileManager);
			        	} else if("03".equals(fileManager.getShareType())) {
			        		fileMapper.saveFileShareGroup(fileManager);
			        	}
			        }
		        }
    		}else {
    			throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
    		}
    	
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[uploadFile] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[uploadFile] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		return ResponseEntity.status(HttpStatus.OK)
    				.body(FileUploadResponseDto
	    				.builder()
	                    .resultCode(e.getErrorCode())
	                    .resultMsg(e.getMessage())
	                    .build());
    	}catch (Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(FileUploadResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
    	
        LocalDateTime startDt = fileManager.getStartDt();
        LocalDateTime endDt = fileManager.getEndDt();

        String pattern = FormatUtils.PATTERNS.WITH_TIME;
        String formattedStartDt = FormatUtils.dateToStringByPattern(startDt, pattern);
        String formattedEndDt = FormatUtils.dateToStringByPattern(endDt, pattern);

        return ResponseEntity.status(HttpStatus.OK)
				.body(FileUploadResponseDto
						.builder()
		                .roomCode(fileManager.getRoomCode())
		                .fileRealNm(fileManager.getFileRealNm())
		                .fileNm(fileManager.getFileNm())
		                .filePath(fileManager.getFilePath())
		                .startDt(formattedStartDt)
		                .endDt(formattedEndDt)
		                .shareType(fileManager.getShareType())
		                .fileUrl(fileManager.getFileUrl())
		                .fileSize(fileManager.getFileSize())
		                .fileSeq(fileManager.getFileSeq())
		                .resultCode("000000")
		                .resultMsg(commonConstant.M_000000)
						.build());
    }

    private String generateFileNm(MultipartFile file, String room_no) {
        String original = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(original);
        String withoutExtension = FilenameUtils.removeExtension(original);
        SimpleDateFormat getDate = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        Date now = new Date();

        String fileNm = "";
        if(!StringUtil.isEmpty(room_no)) {
        	fileNm = String.join("-", room_no, getDate.format(now));
        } else {
        	fileNm = String.join("-", getDate.format(now));
        }
        
        fileNm = fileNm + "." + extension;

        return fileNm;
    }

    private String getFilePath(String fileName, String svcCode) {
        String UPLOAD_PATH = "black/"+svcCode;

        return UPLOAD_PATH + "/" + fileName;
    }

    private FileUploadRequestDto generateFileManager(FileUploadRequestDto fileUploadRequestDto) {
        MultipartFile file 			= fileUploadRequestDto.getFile();
        String fileRealNm 			= file.getOriginalFilename();
        String fileNm 				= file.getOriginalFilename();
        String serverFileNm 		= generateFileNm(file, fileUploadRequestDto.getRoomCode());
        String extension 			= FilenameUtils.getExtension(fileRealNm);
        String userId 				= fileUploadRequestDto.getUserId();
        String svcCode 				= fileUploadRequestDto.getSvcCode();
        String convertedFileSize	= convertFileSizeToEntityForm(file.getSize());
        String shareType			= fileUploadRequestDto.getShareType();
        String targetGroup 			= fileUploadRequestDto.getTargetGroup();
        String targetId 			= fileUploadRequestDto.getTargetId();
        String fileType 			= "01";
        
        if(
        	extension.toUpperCase(Locale.ENGLISH).equals("VOD") || extension.toUpperCase(Locale.ENGLISH).equals("MP4")
        	|| extension.toUpperCase(Locale.ENGLISH).equals("AVI") || extension.toUpperCase(Locale.ENGLISH).equals("MOV")
        	|| extension.toUpperCase(Locale.ENGLISH).equals("MPEG") || extension.toUpperCase(Locale.ENGLISH).equals("OGG")
        	|| extension.toUpperCase(Locale.ENGLISH).equals("RM") || extension.toUpperCase(Locale.ENGLISH).equals("FLV")
        	|| extension.toUpperCase(Locale.ENGLISH).equals("WMV") || extension.toUpperCase(Locale.ENGLISH).equals("WEBM")
          ) {
        	fileType = "02";
        }

        LocalDateTime endDt = LocalDateTime.now().plusWeeks(1);
        LocalDateTime now = LocalDateTime.now();

        fileUploadRequestDto.setFileRealNm(fileRealNm);
        fileUploadRequestDto.setFileNm(fileNm);
        fileUploadRequestDto.setFilePath(getFilePath(serverFileNm, svcCode));
        fileUploadRequestDto.setFileExt(extension);
        fileUploadRequestDto.setFileType(fileType);
        fileUploadRequestDto.setFileSize(convertedFileSize);
        fileUploadRequestDto.setEndDt(endDt);
        fileUploadRequestDto.setStartDt(now);
        fileUploadRequestDto.setRegId(userId);
        fileUploadRequestDto.setRegDt(now);
        fileUploadRequestDto.setUdtId(userId);
        fileUploadRequestDto.setUdtDt(now);
        fileUploadRequestDto.setDelYn("N");
        fileUploadRequestDto.setShareType(shareType);
        fileUploadRequestDto.setTargetGroup(targetGroup);
        fileUploadRequestDto.setTargetId(targetId);

        return fileUploadRequestDto;
    }

    private String uploadToStorage(MultipartFile file, FileUploadRequestDto fileManager) throws IOException {
    	AmazonS3 s3Client = null;
    	String bucket = attachBucket;
    	String domain = fileDomain;
        File oldFile = null;
        File newFile = null;
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        FileAse256 fileEnc = new FileAse256(secretKey);

        String filePath = fileManager.getFilePath();
        
        try {
        	FileUtils fileUtil = FileUtils.getInstance();
        	fileUtil.makeNewFile(tmpPath+"/"+fileManager.getFilePath());
        	oldFile = multipartToFile(file);
        	newFile = new File(tmpPath+"/"+fileManager.getFilePath());
        	
        	fileEnc.encrypt(oldFile, newFile);
        	
        	MultipartFile upFile = getMultipartFile(newFile);
        	
        	ObjectMetadata objMeta = new ObjectMetadata();

        	byte[] bytes = IOUtils.toByteArray(upFile.getInputStream());
        	objMeta.setContentLength(bytes.length);

        	// 파일유형에 따른 버킷명 변경
        	// fileManager.setVodType("rec");
    		if ("02".equals(fileManager.getFileType())) {
    			if("vod".equals(fileManager.getVodType())) {
        			bucket = vodBucket;
        			domain = vodDomain;
    				filePath = "vod/" + filePath;
    			} else if("rec".equals(fileManager.getVodType())){
        			bucket = recBucket;
        			domain = recDomain;
    				filePath = "rec/" + filePath;
    			}
    		}

    		// s3Client = awsS3Helper.getS3Client();
    		s3Client = makingRequestsWithIAMTempCredentials.stsWithIAMTempCredentials(bucket);
            
        	s3Client.putObject(new PutObjectRequest(bucket, filePath, upFile.getInputStream(), objMeta)
        			//.withCannedAcl(CannedAccessControlList.PublicRead));
        			.withCannedAcl(CannedAccessControlList.Private));
        	fileUtil.delFile(tmpPath+"/"+file.getOriginalFilename()); // 기존파일 삭제
        	fileUtil.delFile(tmpPath+"/"+fileManager.getFilePath()); // 기존파일 삭제
        } catch (Exception e) {
        	e.printStackTrace();
		}
        return s3Client.getUrl(bucket, filePath).toString();
    }

    /**
     * 파일 사이즈를 DB 컬럼 규격으로 변환 처리 (KB)
     * @param byteFileSize - 실제 파일 사이즈 (Byte)
     * @return 파일 사이즈 (KB)
     */
    private String convertFileSizeToEntityForm(long byteFileSize) {
        long MINIMUM_SIZE = 1;

        long converted = byteFileSize / 1000;

        return String.valueOf(converted == 0 ? MINIMUM_SIZE : converted);
    }
    
    public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException 
    {
        File convFile = new File( tmpPath+"/"+multipart.getOriginalFilename());
        FileUtils fileUtil = FileUtils.getInstance();
        try {
			fileUtil.makeDir(tmpPath);
	        
	        multipart.transferTo(convFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return convFile;
    }

    public MultipartFile getMultipartFile(File file) throws IOException {
        FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

        try {
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            
            if(input != null) {
            	input.close();
            }
            if(os != null) {
            	os.close();
            }
        } catch (IOException ex) {
            // do something.
        }

        MultipartFile mFile = new CommonsMultipartFile(fileItem);
        return mFile;
    }

	public FileUploadWhiteboardResponseDto whiteboardUploadFile(HttpServletRequest request, @Valid FileUploadWhiteboardRequestDto fileManagerDto) {
		
		HashMap<String, Object> tokenMap = new HashMap<String, Object>();
		FileUploadWhiteboardResponseDto responseDto = new FileUploadWhiteboardResponseDto();;
		
		try {
			logger.info("[whiteboardUploadFile] " + fileManagerDto.toStringShortPrefix());
    		
    		tokenMap.clear();
    		tokenMap.put("roomToken", fileManagerDto.getRoomToken());
    		tokenMap.put("userId", fileManagerDto.getUserId());
    		tokenMap.put("userNm", fileManagerDto.getUserNm());
    		tokenMap.put("roomCode", fileManagerDto.getRoomCode());
			
			RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, tokenMap);
    		if("000000".equals(roomTokenInfo.getResultCode())) {
    			fileManagerDto.setSvcCode(roomTokenInfo.getSvcCode());
				String objectKey = generateImageObjectKey(fileManagerDto.getRoomCode(), fileManagerDto.getFileExtension());
				byte[] decodedBytes = Base64.getDecoder().decode(fileManagerDto.getEncodedFile());
				InputStream fileStream = new ByteArrayInputStream(decodedBytes);
		        ObjectMetadata objectMetadata = new ObjectMetadata();
		        objectMetadata.setContentType(Mimetypes.getInstance().getMimetype(fileManagerDto.getFileName()));
		        objectMetadata.setContentLength(decodedBytes.length);
		        PutObjectRequest putObjectRequest = new PutObjectRequest(attachBucket, objectKey, fileStream, objectMetadata);
		        putObjectRequest.setCannedAcl(CannedAccessControlList.Private);
		        AmazonS3 s3Client = makingRequestsWithIAMTempCredentials.stsWithIAMTempCredentials(attachBucket);
		        s3Client.putObject(putObjectRequest);
		        String imageUrl = awsCloudFrontURL.getUrl(objectKey, fileDomain, "canned");
		        responseDto.setImageUrl(imageUrl);
		        responseDto.setRoomCode(fileManagerDto.getRoomCode());
		        responseDto.setResultCode("000000");
		        responseDto.setResultMsg(commonConstant.M_000000);
		        
    		}else {
    			throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
    		}
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[whiteboardUploadFile] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[whiteboardUploadFile] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		responseDto.setResultCode(e.getErrorCode());
    		responseDto.setResultMsg(e.getMessage());
		}catch (Exception e) {
			logger.info("000003", commonConstant.M_000003, e.getMessage());
			responseDto.setResultCode("000003");
    		responseDto.setResultMsg(commonConstant.M_000003);
		}
		
		return responseDto;
	}
	
	String generateImageObjectKey(String roomCode, String fileExtension) {
        String uniqueId = UUID.randomUUID().toString();

        StringJoiner stringJoiner = new StringJoiner("/");
        stringJoiner.add(whiteboardFilePath);
        stringJoiner.add(roomCode);
        stringJoiner.add(uniqueId);
        String joined = stringJoiner.toString();

        return joined + "." + fileExtension;
    }
	
	String generateImageURL(String domain, String objectKey) {
        return "https://" + domain + "/" + objectKey;
    }
}
