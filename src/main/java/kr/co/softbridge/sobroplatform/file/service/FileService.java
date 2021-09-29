package kr.co.softbridge.sobroplatform.file.service;

import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.RoomVerifyResponseDto;
import kr.co.softbridge.sobroplatform.commons.exception.ApiException;
import kr.co.softbridge.sobroplatform.commons.service.CommonTokenService;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.file.dto.FileListDto;
import kr.co.softbridge.sobroplatform.file.dto.FileListResponseDto;
import kr.co.softbridge.sobroplatform.file.mapper.FileMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 파일 service
 */
@Service
public class FileService {
	
	private static final Logger logger = LogManager.getLogger(FileService.class);

    @Autowired
    private FileMapper fileMapper;
    
	@Autowired
    private CommonTokenService commonTokenService;
    
    
    public ResponseEntity<FileListResponseDto> getFilesList(HttpServletRequest request, HashMap<String, Object> paramMap) {
    	List<FileListDto> fileList = null;
    	int totalCount = 0;
    	try {
    		logger.info("[getFilesList] paramMap=[" + paramMap.toString() + "]");
    		
    		if(StringUtil.isEmpty((String) paramMap.get("roomCode"))
    				||StringUtil.isEmpty((String) paramMap.get("roomToken"))
    				||StringUtil.isEmpty((String) paramMap.get("userId"))
    				||StringUtil.isEmpty((String) paramMap.get("userNm"))
    		) {
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
    		
			/* roomToken 검증 */
			RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, paramMap);
    		if("000000".equals(roomTokenInfo.getResultCode())) {
				/* svcCode 세팅 */
    			paramMap.put("svcCode", roomTokenInfo.getSvcCode());
    		
	    		if("".equals((String) paramMap.get("userId"))) {
	    			paramMap.put("userId", "");
	    		}
	    		if("".equals((String) paramMap.get("targetGroup"))) {
	    			paramMap.put("targetGroup", "");
	    		}
	    		/* room 목록 총 개수(페이징 처리를 위해) */
    			totalCount = fileMapper.getFileListCount(paramMap);
    			
    			int pageNum = Integer.parseInt(StringUtil.null2void(paramMap.get("page"), "1"));
    			int pageSize = Integer.parseInt(StringUtil.null2void(paramMap.get("pageSize"), "50"));
    			if(pageNum == 1) {
    				pageNum = pageNum - 1;
    			}else {
    				pageNum = (pageNum -1) * pageSize;
    			}
    			paramMap.put("pageNum", pageNum);
    			paramMap.put("pageSize", pageSize);
	    		fileList = fileMapper.getFileList(paramMap);
    		}else {
    			throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
    		}
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[getFilesList] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[getFilesList] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		return ResponseEntity.status(HttpStatus.OK)
					.body(FileListResponseDto
							.builder()
							.resultCode(e.getErrorCode())
                            .resultMsg(e.getMessage())
							.build());
    	} catch(Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(FileListResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
    	}
    	return ResponseEntity.status(HttpStatus.OK)
				.body(FileListResponseDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.fileCnt(totalCount)
						.fileList(fileList)
						.build());
    }
}
