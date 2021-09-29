package kr.co.softbridge.sobroplatform.meetinglog.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.RoomVerifyResponseDto;
import kr.co.softbridge.sobroplatform.commons.exception.ApiException;
import kr.co.softbridge.sobroplatform.commons.mapper.CommonTokenMapper;
import kr.co.softbridge.sobroplatform.commons.service.CommonTokenService;
import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.file.dto.FileListResponseDto;
import kr.co.softbridge.sobroplatform.meetinglog.dto.MeetingLogDto;
import kr.co.softbridge.sobroplatform.meetinglog.dto.MeetingLogRequestDto;
import kr.co.softbridge.sobroplatform.meetinglog.mapper.MettingLogMapper;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizCreateResponseDto;

@Service
public class MeetingLogService {
	
	private static final Logger logger = LogManager.getLogger(MeetingLogService.class);
	
    @Value("${jwt.secret}")
    private String secret; 
    
    @Value("${jwt.type.BRO}")
    private String BRO; 
    
    @Autowired
    private MettingLogMapper mettingLogMapper;

	@Autowired
    private CommonTokenService commonTokenService;
	
	@Autowired
    private CommonTokenMapper commonTokenMapper;
	
	@Autowired
	private MonitoringLogService monitoringLogService;

	public ResponseEntity<MeetingLogDto> selectMeetingLog(
			HttpServletRequest request,
			HashMap<String, Object> paramMap) throws Exception {
		MeetingLogDto meetingLogResult = null;
		try {
    		if(StringUtil.isEmpty((String) paramMap.get("roomCode"))
    				||StringUtil.isEmpty((String) paramMap.get("userId"))
    				||StringUtil.isEmpty((String) paramMap.get("userNm"))
    				||StringUtil.isEmpty((String) paramMap.get("roomToken"))
    		) {
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
			/* roomToken 검증 */
			RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, paramMap);
			if("000000".equals(roomTokenInfo.getResultCode())) {
	    		meetingLogResult = getMeetingLog(paramMap);
	    		
	    		if(meetingLogResult == null) {
	    			return ResponseEntity.status(HttpStatus.OK)
	    	    			.body(MeetingLogDto
	    	    					.builder()
	    	    					.resultCode("000000")
	    	    					.resultMsg(commonConstant.M_000000)
	    	    					.build()); 
	    		}
			}else {
				throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
			}
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[selectMeetingLog] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[selectMeetingLog] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(MeetingLogDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		} catch(Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(MeetingLogDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		
		return ResponseEntity.status(HttpStatus.OK)
    			.body(MeetingLogDto
    					.builder()
    					.roomCode(meetingLogResult.getRoomCode())
    					.meetingLogSeq(meetingLogResult.getMeetingLogSeq())
    					.contents(meetingLogResult.getContents())
    					.resultCode("000000")
    					.resultMsg(commonConstant.M_000000)
    					.build()); 
	}

	private MeetingLogDto getMeetingLog(HashMap<String, Object> paramMap) {
		return mettingLogMapper.getMeetingLog(paramMap);
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<MeetingLogDto> saveMeetingLog(
			HttpServletRequest request,
			MeetingLogRequestDto param) throws Exception {
		MeetingLogDto meetingLogResult = null;
		try {
			
			if(StringUtil.isEmpty((String) param.getRoomCode())
    				||StringUtil.isEmpty((String) param.getUserId())
    				||StringUtil.isEmpty((String) param.getUserNm())
    				||StringUtil.isEmpty((String) param.getRoomToken())
    				||StringUtil.isEmpty((String) param.getContents())
    				||StringUtil.isEmpty((String) param.getSaveType())
    		) {
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
			
			/* roomToken 검증 */
    		HashMap<String, Object> paramMap = new HashMap<String, Object>();
    		paramMap.put("roomCode", param.getRoomCode());
    		paramMap.put("roomToken", param.getRoomToken());
    		paramMap.put("userId", param.getUserId());
    		paramMap.put("userNm", param.getUserNm());
    		paramMap.put("contents", param.getContents());
			RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, paramMap);
			if("000000".equals(roomTokenInfo.getResultCode())) {
				param.setSvcCode(roomTokenInfo.getSvcCode());
				int contentsCount = getMeetingLogCnt(paramMap);
				if(param.getSaveType().equals(commonConstant.SAVE_CRATE)) {
					if( contentsCount == 0 ) insertMettingLog(paramMap);
					else updateMettingLog(paramMap);
				} else if(param.getSaveType().equals(commonConstant.SAVE_UPDATE)) {
					if( contentsCount == 0 ) insertMettingLog(paramMap);
					else updateMettingLog(paramMap);
				} else {
					return ResponseEntity.status(HttpStatus.OK)
			    			.body(MeetingLogDto
			    					.builder()
			    					.resultCode("000002")
			    					.resultMsg(commonConstant.M_000002)
			    					.build());
				}
	    		meetingLogResult = getMeetingLog(paramMap);
			}else {
				throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
			}
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[saveMeetingLog] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[saveMeetingLog] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(MeetingLogDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		} catch(Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(MeetingLogDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		
		return ResponseEntity.status(HttpStatus.OK)
    			.body(MeetingLogDto
    					.builder()
    					.roomCode(meetingLogResult.getRoomCode())
    					.meetingLogSeq(meetingLogResult.getMeetingLogSeq())
    					.contents(meetingLogResult.getContents())
    					.resultCode("000000")
    					.resultMsg(commonConstant.M_000000)
    					.build());
	}

	private int getMeetingLogCnt(HashMap<String, Object> paramMap) {
		return mettingLogMapper.getMeetingLogCnt(paramMap);
	}

	private void updateMettingLog(HashMap<String, Object> paramMap) {
		mettingLogMapper.updateMettingLog(paramMap);
	}

	private void insertMettingLog(HashMap<String, Object> paramMap) {
		mettingLogMapper.insertMettingLog(paramMap);
	}

}
