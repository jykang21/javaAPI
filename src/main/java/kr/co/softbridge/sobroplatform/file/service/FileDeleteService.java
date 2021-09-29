package kr.co.softbridge.sobroplatform.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import kr.co.softbridge.sobroplatform.commons.MakingRequestsWithIAMTempCredentials;
import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.CommonResDto;
import kr.co.softbridge.sobroplatform.commons.dto.RoomVerifyResponseDto;
import kr.co.softbridge.sobroplatform.commons.exception.ApiException;
import kr.co.softbridge.sobroplatform.commons.service.CommonTokenService;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.file.dto.FileDeleteRequestDto;
import kr.co.softbridge.sobroplatform.file.dto.FileDeleteResponseDto;
import kr.co.softbridge.sobroplatform.file.dto.FileManagerDto;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadResponseDto;
import kr.co.softbridge.sobroplatform.file.mapper.FileMapper;
import kr.co.softbridge.sobroplatform.room.service.RoomService;
import lombok.Getter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * 파일 삭제 service
 */
@Service
public class FileDeleteService {
	private static final Logger logger = LogManager.getLogger(FileDeleteService.class);
    
    @Value("${cloud.aws.s3.vodBucket}")
    @Getter
    private String vodBucket;

    @Value("${cloud.aws.s3.recBucket}")
    @Getter
    private String recBucket;

    @Value("${cloud.aws.s3.attachBucket}")
    @Getter
    private String attachBucket;
    
    @Value("${cloud.aws.whiteboard.filePath}")
    private String whiteboardFilePath;
    
	@Autowired
    private FileMapper fileMapper;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private CommonTokenService commonTokenService;
    
    @Autowired
    private MakingRequestsWithIAMTempCredentials makingRequestsWithIAMTempCredentials;

    /**
     * 파일 삭제
     * @param fileDeleteRequestDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<CommonResDto> deleteFile(HttpServletRequest request, FileDeleteRequestDto fileDeleteRequestDto) {
    	FileDeleteResponseDto deletedFile = new FileDeleteResponseDto();
    	
    	try {
    		logger.info("[deleteFile] " + fileDeleteRequestDto.toStringShortPrefix());
	    	if (
					StringUtil.isEmpty(fileDeleteRequestDto.getRoomCode()) 
					|| StringUtil.isEmpty(fileDeleteRequestDto.getRoomToken())
					|| StringUtil.isEmpty(fileDeleteRequestDto.getUserId())
					|| StringUtil.isEmpty(fileDeleteRequestDto.getUserNm())
			) {
	    		throw new ApiException("000001", commonConstant.M_000001);
			}
	    	
	    	HashMap<String, Object> dbMap = new HashMap<String, Object>();
	    	dbMap.clear();
	    	dbMap.put("roomToken", fileDeleteRequestDto.getRoomToken());
	    	dbMap.put("roomCode", fileDeleteRequestDto.getRoomCode());
	    	dbMap.put("userId", fileDeleteRequestDto.getUserId());
	    	dbMap.put("userNm", fileDeleteRequestDto.getUserNm());
	    	RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
        	if("000000".equals(roomTokenInfo.getResultCode())) {
		        specifyParamsForDeleteFile(fileDeleteRequestDto);
		        String managerId = roomService.checkRoomManagerId(dbMap);
		        deletedFile = getFileByIdToDeleteFile(fileDeleteRequestDto);
		        
		        logger.info("deletedFile=[" + deletedFile + "]");
		        if(deletedFile != null) {
		        	if (
		        		fileDeleteRequestDto.getUserId().equals(deletedFile.getRegId())
		        		|| fileDeleteRequestDto.getUserId().equals(managerId)
		        	) {
				        String bucket = attachBucket;
				        String filePath = deletedFile.getFilePath();
				        if ("02".equals(deletedFile.getFileType())) {
			    			if("vod".equals(deletedFile.getVodType())) {
			        			bucket = vodBucket;
			    				filePath = "vod/" + filePath;
			    			} else if("rec".equals(deletedFile.getVodType())){
			        			bucket = recBucket;
			    				filePath = "rec/" + filePath;
			    			}
			    		}
				        AmazonS3 s3Client = makingRequestsWithIAMTempCredentials.stsWithIAMTempCredentials(bucket);
				
				        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, filePath);
				        s3Client.deleteObject(deleteObjectRequest);
				        logger.info("success");
				        fileMapper.deleteShareTargetFile(fileDeleteRequestDto);
				        fileMapper.deleteShareGroupFile(fileDeleteRequestDto);
				        fileMapper.deleteFile(fileDeleteRequestDto);
				        fileMapper.deleteMasterFile(fileDeleteRequestDto);
		        	} else {
			        	throw new ApiException("003002", commonConstant.M_003002);
		        	}
		        }else {
	        		throw new ApiException("004001", commonConstant.M_004001);
		        }
	    	}else {
	    		throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
	    	}
	    	
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[deleteFile] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[deleteFile] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		return ResponseEntity.status(HttpStatus.OK)
    				.body(CommonResDto
	    				.builder()
	                    .resultCode(e.getErrorCode())
	                    .resultMsg(e.getMessage())
	                    .build());
    	}catch (Exception e) {
			e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.OK)
    				.body(CommonResDto
	    				.builder()
	                    .resultCode("000003")
	                    .resultMsg(commonConstant.M_000003)
	                    .build());
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(CommonResDto
    				.builder()
                    .resultCode("000000")
                    .resultMsg(commonConstant.M_000000)
                    .build());
    }

    /**
     * 파일 삭제 요청 데이터 전처리
     * @param fileDeleteRequestDto
     */
    private void specifyParamsForDeleteFile(FileDeleteRequestDto fileDeleteRequestDto) {
        LocalDateTime now = LocalDateTime.now();
        fileDeleteRequestDto.setUdtDt(now);
    }

    private FileDeleteResponseDto getFileByIdToDeleteFile(FileDeleteRequestDto fileDeleteRequestDto) {
        FileManagerDto fileManagerDto = FileManagerDto.builder()
                .roomCode(fileDeleteRequestDto.getRoomCode())
                .fileSeq(fileDeleteRequestDto.getFileSeq())
                .build();

        return fileMapper.getFileByIdToDeleteFile(fileManagerDto);
    }

    /**
     * 화이트보드 파일 삭제(roomCode 기준으로 삭제)
     * @param paramMap
     */
	public void whiteboardDelete(HashMap<String, Object> paramMap) {
		
		String filePath = whiteboardFilePath + "/" + paramMap.get("roomCode");
		
		AmazonS3 s3Client = makingRequestsWithIAMTempCredentials.stsWithIAMTempCredentials(attachBucket);
		
		for (S3ObjectSummary file : s3Client.listObjects(attachBucket, filePath).getObjectSummaries()){
			s3Client.deleteObject(attachBucket, file.getKey());
	    }
	}
}
