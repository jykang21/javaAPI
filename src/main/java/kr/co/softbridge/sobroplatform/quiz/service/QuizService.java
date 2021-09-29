package kr.co.softbridge.sobroplatform.quiz.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.RoomVerifyResponseDto;
import kr.co.softbridge.sobroplatform.commons.exception.ApiException;
import kr.co.softbridge.sobroplatform.commons.service.CommonTokenService;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.commons.util.Util;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizCreateRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizCreateResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDeleteRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDetailRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDetailResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDtlDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDtlListResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizInfoListResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.AnswerSubmitRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizChangeRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizCommonResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizInfoQueryDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizListDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizListRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizListResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizUpdateRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizUserDto;
import kr.co.softbridge.sobroplatform.quiz.mapper.QuizMapper;

@Service
public class QuizService {
	
	private static final Logger logger = LogManager.getLogger(QuizService.class);
	
	@Autowired
    private CommonTokenService commonTokenService;
	@Autowired
    private QuizMapper quizMapper;

	/* 퀴즈 생성 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<QuizCreateResponseDto> quizCreate(HttpServletRequest request, QuizCreateRequestDto quizCreateRequestDto) {
		
		String quizNo = "";
		
		try {
			logger.info("[quizCreate] quizCreateRequestDto=" + quizCreateRequestDto.toString());
			
			/* 파라미터 검증 */
			if (
					StringUtil.isEmpty(quizCreateRequestDto.getRoomCode())
					|| StringUtil.isEmpty(quizCreateRequestDto.getRoomToken())
					|| StringUtil.isEmpty(quizCreateRequestDto.getUserId())
					|| StringUtil.isEmpty(quizCreateRequestDto.getUserNm())
					|| StringUtil.isEmpty(quizCreateRequestDto.getTitle())
					|| StringUtil.isEmpty(quizCreateRequestDto.getRunningTime())
					|| StringUtil.isEmpty(quizCreateRequestDto.getQuizInfoList().get(0).getContents())
					|| StringUtil.isEmpty(quizCreateRequestDto.getQuizInfoList().get(0).getQuizAnswer())
					|| StringUtil.isEmpty(quizCreateRequestDto.getQuizInfoList().get(0).getQuizDtlList().get(0).getOptionContents())
			) {
	    		throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/* runningTime 숫자 체크 */
			if(!Util.checkNumberic(quizCreateRequestDto.getRunningTime())) {
				throw new ApiException("005008", commonConstant.M_005008);
			}
			
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
	    	dbMap.clear();
	    	dbMap.put("roomToken", quizCreateRequestDto.getRoomToken());
	    	dbMap.put("roomCode", quizCreateRequestDto.getRoomCode());
	    	dbMap.put("userId", quizCreateRequestDto.getUserId());
	    	dbMap.put("userNm", quizCreateRequestDto.getUserNm());
	    	RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
        	if("000000".equals(roomTokenInfo.getResultCode())) {
        		/* 로그용 */
        		quizCreateRequestDto.setSvcCode(roomTokenInfo.getSvcCode());
        		
				quizNo = quizMapper.getQuizNo();
				for(int i = 0; i < quizCreateRequestDto.getQuizInfoList().size(); i++) {
					String quizSubNo = quizCreateRequestDto.getQuizInfoList().get(i).getQuizSubNo();
					
					dbMap.clear();
					dbMap.put("quizNo", quizNo);
					dbMap.put("quizSubNo", quizSubNo);
					dbMap.put("roomCode", quizCreateRequestDto.getRoomCode());
					dbMap.put("title", quizCreateRequestDto.getTitle());
					dbMap.put("contents", quizCreateRequestDto.getQuizInfoList().get(i).getContents());
					dbMap.put("status", "01"); /* 미발행 */
					dbMap.put("runningTime", quizCreateRequestDto.getRunningTime());
					dbMap.put("quizAnswer", quizCreateRequestDto.getQuizInfoList().get(i).getQuizAnswer());
					dbMap.put("delYn", "N");
					dbMap.put("regId", quizCreateRequestDto.getUserId());
					dbMap.put("regDt", quizCreateRequestDto.getNowTime());
					
					Boolean checkInfo = Util.resultCheck(quizMapper.insertQuizInfo(dbMap));
					
					if(checkInfo) {
						for(int j = 0; j < quizCreateRequestDto.getQuizInfoList().get(i).getQuizDtlList().size(); j++) {
							dbMap.put("optionNo", quizCreateRequestDto.getQuizInfoList().get(i).getQuizDtlList().get(j).getOptionNo());
							dbMap.put("optionContents", quizCreateRequestDto.getQuizInfoList().get(i).getQuizDtlList().get(j).getOptionContents());
							
							Boolean checkDtl = Util.resultCheck(quizMapper.insertQuizDtl(dbMap));
							
							if(!checkDtl) {
								throw new ApiException("005001", commonConstant.M_005001, "TB_QUIZ_DTL 저장 실패");
							}
						}
					}else {
						throw new ApiException("005001", commonConstant.M_005001, "TB_QUIZ_LIST 저장 실패");
					}
				}
			
        	}else {
				throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
			}
			
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[quizCreate] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[quizCreate] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCreateResponseDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		}catch (Exception e) {
			logger.info("[quizCreate] ERROR_CODE=000003, ERROR_MSG=" + e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCreateResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		
		return ResponseEntity.status(HttpStatus.OK)
				.body(QuizCreateResponseDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.quizNo(quizNo)
						.build());
	}

	
	/* 퀴즈 목록 */
	public ResponseEntity<QuizListResponseDto> quizList(HttpServletRequest request, QuizListRequestDto quizListRequestDto) {
		
		List<QuizListDto> quizList = null;
		
		try {
			logger.info("[quizList] quizListRequestDto=" + quizListRequestDto.toString());
			
			/* 파라미터 검증 */
			if (
					StringUtil.isEmpty(quizListRequestDto.getRoomCode())
					|| StringUtil.isEmpty(quizListRequestDto.getRoomToken())
					|| StringUtil.isEmpty(quizListRequestDto.getUserId())
					|| StringUtil.isEmpty(quizListRequestDto.getUserNm())
//					|| StringUtil.isEmpty(quizListRequestDto.getManagerAuth())
					|| StringUtil.isEmpty(quizListRequestDto.getPresenterYn())
			) {
	    		throw new ApiException("000001", commonConstant.M_000001);
			}
			
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
	    	dbMap.clear();
	    	dbMap.put("roomToken", quizListRequestDto.getRoomToken());
	    	dbMap.put("roomCode", quizListRequestDto.getRoomCode());
	    	dbMap.put("userId", quizListRequestDto.getUserId());
	    	dbMap.put("userNm", quizListRequestDto.getUserNm());
	    	RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
        	if("000000".equals(roomTokenInfo.getResultCode())) {
        		/* 로그용 */
        		quizListRequestDto.setSvcCode(roomTokenInfo.getSvcCode());
			
				dbMap.put("roomCode", quizListRequestDto.getRoomCode());
//				if("U".equals(quizListRequestDto.getManagerAuth())) {
				if("U".equals(quizListRequestDto.getPresenterYn())) {
					dbMap.put("status", "03");
//				}else if("P".equals(quizListRequestDto.getManagerAuth())) {
				}else if("P".equals(quizListRequestDto.getPresenterYn())) {
					//
				}else {
					throw new ApiException("003003", commonConstant.M_003003);	
				}
				quizList = quizMapper.getQuizInfoList(dbMap);
				
			}else {
    			throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
    		}
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[quizList] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[quizList] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizListResponseDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		}catch (Exception e) {
			logger.info("[quizList] ERROR_CODE=000003, ERROR_MSG=" + e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizListResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(QuizListResponseDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.quizList(quizList)
						.build());
	}

	/* 퀴즈 수정 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<QuizCommonResponseDto> quizUpdate(HttpServletRequest request, QuizUpdateRequestDto quizUpdateRequestDto) {
		
		try {
			logger.info("[quizUpdate] quizUpdateRequestDto=" + quizUpdateRequestDto.toString());
			
			/* 파라미터 검증 */
			if (
					StringUtil.isEmpty(quizUpdateRequestDto.getRoomCode())
					|| StringUtil.isEmpty(quizUpdateRequestDto.getRoomToken())
					|| StringUtil.isEmpty(quizUpdateRequestDto.getUserId())
					|| StringUtil.isEmpty(quizUpdateRequestDto.getUserNm())
			) {
	    		throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/* runningTime 숫자 체크 */
			if(!Util.checkNumberic(quizUpdateRequestDto.getRunningTime())) {
				throw new ApiException("005008", commonConstant.M_005008);
			}
			
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
	    	dbMap.clear();
	    	dbMap.put("roomToken", quizUpdateRequestDto.getRoomToken());
	    	dbMap.put("roomCode", quizUpdateRequestDto.getRoomCode());
	    	dbMap.put("userId", quizUpdateRequestDto.getUserId());
	    	dbMap.put("userNm", quizUpdateRequestDto.getUserNm());
	    	RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
        	if("000000".equals(roomTokenInfo.getResultCode())) {
        		/* 로그용 */
        		quizUpdateRequestDto.setSvcCode(roomTokenInfo.getSvcCode());
        		
        		dbMap.clear();
        		dbMap.put("roomCode", quizUpdateRequestDto.getRoomCode());
				dbMap.put("quizNo", quizUpdateRequestDto.getQuizNo());
        		List<QuizInfoQueryDto> quizInfoList = quizMapper.getQuizInfo(dbMap);
        		if(!"01".equals(quizInfoList.get(0).getStatus())) {
        			throw new ApiException("005007", commonConstant.M_005007);
        		}
			
        		dbMap.clear();
        		dbMap.put("quizNo", quizUpdateRequestDto.getQuizNo());
        		Boolean deleteDtl = Util.resultCheck(quizMapper.deleteQuizDtl(dbMap));
        		if(deleteDtl) {
        			Boolean deleteInfo = Util.resultCheck(quizMapper.deleteQuizInfo(dbMap));
        			if(deleteInfo) {
    					
        				for(int i = 0; i < quizUpdateRequestDto.getQuizInfoList().size(); i++) {
        					
        					dbMap.clear();
        					dbMap.put("quizNo", quizUpdateRequestDto.getQuizNo());
        					dbMap.put("quizSubNo", quizUpdateRequestDto.getQuizInfoList().get(i).getQuizSubNo());
        					dbMap.put("roomCode", quizUpdateRequestDto.getRoomCode());
        					dbMap.put("title", quizUpdateRequestDto.getTitle());
        					dbMap.put("contents", quizUpdateRequestDto.getQuizInfoList().get(i).getContents());
        					dbMap.put("status", "01"); /* 미발행 */
        					dbMap.put("runningTime", quizUpdateRequestDto.getRunningTime());
        					dbMap.put("quizAnswer", quizUpdateRequestDto.getQuizInfoList().get(i).getQuizAnswer());
        					dbMap.put("delYn", "N");
        					dbMap.put("regId", quizUpdateRequestDto.getUserId());
        					dbMap.put("regDt", quizUpdateRequestDto.getNowTime());
        					
        					Boolean checkInfo = Util.resultCheck(quizMapper.insertQuizInfo(dbMap));
        					
        					if(checkInfo) {
        						for(int j = 0; j < quizUpdateRequestDto.getQuizInfoList().get(i).getQuizDtlList().size(); j++) {
        							dbMap.put("optionNo", quizUpdateRequestDto.getQuizInfoList().get(i).getQuizDtlList().get(j).getOptionNo());
        							dbMap.put("optionContents", quizUpdateRequestDto.getQuizInfoList().get(i).getQuizDtlList().get(j).getOptionContents());
        							
        							Boolean checkDtl = Util.resultCheck(quizMapper.insertQuizDtl(dbMap));
        							
        							if(!checkDtl) {
        								throw new ApiException("005002", commonConstant.M_005002, "TB_QUIZ_DTL 저장 실패");
        							}
        						}
        					}else {
        						throw new ApiException("005002", commonConstant.M_005002, "TB_QUIZ_LIST 저장 실패");
        					}
        				}
        			}else {
        				throw new ApiException("005002", commonConstant.M_005002, "TB_QUIZ_LIST 삭제 실패");
        			}
        		}else {
        			throw new ApiException("005002", commonConstant.M_005002, "TB_QUIZ_DTL 삭제 실패");
        		}
        		
        		
			
			}else {
				throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
			}
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[quizUpdate] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[quizUpdate] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCommonResponseDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		}catch (Exception e) {
			logger.info("[quizUpdate] ERROR_CODE=000003, ERROR_MSG=" + e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCommonResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(QuizCommonResponseDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}

	/* 퀴즈 삭제 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<QuizCommonResponseDto> quizDelete(HttpServletRequest request, QuizDeleteRequestDto quizDeleteRequestDto) {
		
		try {
			logger.info("[quizDelete] quizDeleteRequestDto=" + quizDeleteRequestDto.toString());
			
			/* 파라미터 검증 */
			if (
					StringUtil.isEmpty(quizDeleteRequestDto.getRoomCode())
					|| StringUtil.isEmpty(quizDeleteRequestDto.getRoomToken())
					|| StringUtil.isEmpty(quizDeleteRequestDto.getUserId())
					|| StringUtil.isEmpty(quizDeleteRequestDto.getUserNm())
					|| StringUtil.isEmpty(quizDeleteRequestDto.getQuizNo())
			) {
	    		throw new ApiException("000001", commonConstant.M_000001);
			}
			
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
			dbMap.clear();
			dbMap.put("roomToken", quizDeleteRequestDto.getRoomToken());
			dbMap.put("roomCode", quizDeleteRequestDto.getRoomCode());
			dbMap.put("userId", quizDeleteRequestDto.getUserId());
			dbMap.put("userNm", quizDeleteRequestDto.getUserNm());
			RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
			if("000000".equals(roomTokenInfo.getResultCode())) {
				/* 로그용 */
				quizDeleteRequestDto.setSvcCode(roomTokenInfo.getSvcCode());
        		
				dbMap.clear();
				dbMap.put("roomCode", quizDeleteRequestDto.getRoomCode());
				dbMap.put("quizNo", quizDeleteRequestDto.getQuizNo());
				List<QuizInfoQueryDto> quizInfoList = quizMapper.getQuizInfo(dbMap);
				if(quizInfoList.size() > 0) {
					if(!quizDeleteRequestDto.getUserId().equals(quizInfoList.get(0).getRegId())) {
						throw new ApiException("003002", commonConstant.M_003002);
					}
				}else {
					throw new ApiException("005006", commonConstant.M_005006);
				}
			
				dbMap.clear();
				dbMap.put("roomCode", quizDeleteRequestDto.getRoomCode());
				dbMap.put("quizNo", quizDeleteRequestDto.getQuizNo());
				dbMap.put("delYn", "Y");
				dbMap.put("uptId", quizDeleteRequestDto.getUserId());
				dbMap.put("uptDt", quizDeleteRequestDto.getNowTime());
				Boolean checkInfo = Util.resultCheck(quizMapper.updateQuizInfoDelYn(dbMap));
				
				if(checkInfo) {
					
					Boolean checkDtl = Util.resultCheck(quizMapper.updateQuizDtlDelYn(dbMap));
					
					if(!checkDtl) {
						throw new ApiException("005003", commonConstant.M_005003, "TB_QUIZ_DTL 저장 실패");
					}
				}else {
					throw new ApiException("005003", commonConstant.M_005003, "TB_QUIZ_INFO 저장 실패");
				}
			
			
			}else {
				throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
			}
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[quizDelete] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[quizDelete] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCommonResponseDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		}catch (Exception e) {
			logger.info("[quizDelete] ERROR_CODE=000003, ERROR_MSG=" + e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCommonResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(QuizCommonResponseDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}
	
	public ResponseEntity<QuizDetailResponseDto> quizDetail(HttpServletRequest request, QuizDetailRequestDto quizDetailRequestDto) {
		
		QuizDetailResponseDto quizDetailResponse = new QuizDetailResponseDto();
		
		try {
			logger.info("[quizDetail] quizDetailRequestDto=" + quizDetailRequestDto.toString());
			
			/* 파라미터 검증 */
			if (
					StringUtil.isEmpty(quizDetailRequestDto.getRoomCode())
					|| StringUtil.isEmpty(quizDetailRequestDto.getRoomToken())
					|| StringUtil.isEmpty(quizDetailRequestDto.getUserId())
					|| StringUtil.isEmpty(quizDetailRequestDto.getUserNm())
					|| StringUtil.isEmpty(quizDetailRequestDto.getQuizNo())
			) {
	    		throw new ApiException("000001", commonConstant.M_000001);
			}
			
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
	    	dbMap.clear();
	    	dbMap.put("roomToken", quizDetailRequestDto.getRoomToken());
	    	dbMap.put("roomCode", quizDetailRequestDto.getRoomCode());
	    	dbMap.put("userId", quizDetailRequestDto.getUserId());
	    	dbMap.put("userNm", quizDetailRequestDto.getUserNm());
	    	RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
        	if("000000".equals(roomTokenInfo.getResultCode())) {
        		/* 로그용 */
        		quizDetailRequestDto.setSvcCode(roomTokenInfo.getSvcCode());
				
				dbMap.clear();
				dbMap.put("roomCode", quizDetailRequestDto.getRoomCode());
				dbMap.put("quizNo", quizDetailRequestDto.getQuizNo());
				dbMap.put("status", quizDetailRequestDto.getStatus());
				
				List<QuizInfoQueryDto> quizInfoList = quizMapper.getQuizInfo(dbMap);
				
				quizDetailResponse.setQuizNo(quizDetailRequestDto.getQuizNo());
				quizDetailResponse.setTitle(quizInfoList.get(0).getTitle());
				quizDetailResponse.setStartDt(quizInfoList.get(0).getStartDt());
				quizDetailResponse.setRunningTime(quizInfoList.get(0).getRunningTime());
				quizDetailResponse.setStatus(quizInfoList.get(0).getStatus());
				ArrayList infoList = new ArrayList<QuizInfoListResponseDto>();
				for(int i = 0; i < quizInfoList.size(); i++) {
					QuizInfoListResponseDto info = new QuizInfoListResponseDto();
					info.setQuizSubNo(quizInfoList.get(i).getQuizSubNo());
					info.setContents(quizInfoList.get(i).getContents());
					info.setQuizAnswer(quizInfoList.get(i).getQuizAnswer());
					
					dbMap.put("quizSubNo", quizInfoList.get(i).getQuizSubNo());
					double totalUserCount = quizMapper.getQuizUserCount(dbMap);
					double selectUserCount = 0;
					List<QuizDtlDto> quizDtl = quizMapper.getQuizDtl(dbMap);
					/* 문제에 응시했는지에 대한 처리 */
					int answerCnt = 0;
					String submitAnwserYn = "N";
					String mySubmitAnswer = "";
					ArrayList dtlList = new ArrayList<QuizDtlListResponseDto>();
					for(int j = 0; j < quizDtl.size(); j++) {
						dbMap.put("userStatus", "");
						QuizDtlListResponseDto dtl = new QuizDtlListResponseDto();
						dtl.setOptionNo(quizDtl.get(j).getOptionNo());
						dtl.setOptionContents(quizDtl.get(j).getOptionContents());
						/* 보기에 따른 응답자 */
						dbMap.put("optionNo", quizDtl.get(j).getOptionNo());
						List<QuizUserDto> quizUser = quizMapper.getQuizUser(dbMap);
						selectUserCount = quizUser.size();
						String userList = "";
						for(int k = 0; k < quizUser.size(); k++) {
							userList += quizUser.get(k).getUserNm();
							if(k != quizUser.size()-1) {
								userList += ", ";
							}
							if(quizDetailRequestDto.getUserId().equals(quizUser.get(k).getUserId())) {
								answerCnt = answerCnt + 1;
								mySubmitAnswer = quizUser.get(k).getSubmitAnswer();
							}
						}
						dtl.setSelectUserList(userList);
						double selectPercent = 0;
						if(totalUserCount != 0) {
							selectPercent = (selectUserCount/totalUserCount)*100;
						}
						String dispPattern = "0.##";
						DecimalFormat form = new DecimalFormat(dispPattern);
						dtl.setSelectUserPercent(form.format(selectPercent));
						dtlList.add(dtl);
					}
					/* 미응시에 대한 처리 */
					if("03".equals(quizDetailResponse.getStatus())) {
						dbMap.clear();
						dbMap.put("quizNo", quizDetailRequestDto.getQuizNo());
						dbMap.put("quizSubNo", quizInfoList.get(i).getQuizSubNo());
						dbMap.put("userStatus", "N");
						List<QuizUserDto> quizUser = quizMapper.getQuizUser(dbMap);
						selectUserCount = quizUser.size();
						String userList = "";
						for(int k = 0; k < quizUser.size(); k++) {
							userList += quizUser.get(k).getUserNm();
							if(k != quizUser.size()-1) {
								userList += ", ";
							}
						}
						QuizDtlListResponseDto dtl = new QuizDtlListResponseDto();
						double selectPercent = 0;
						if(totalUserCount != 0) {
							selectPercent = (selectUserCount/totalUserCount)*100;
						}
						String dispPattern = "0.##";
						DecimalFormat form = new DecimalFormat(dispPattern);
						
						dtl.setOptionNo(Integer.toString(quizDtl.size()+1));
						dtl.setOptionContents("미응시");
						dtl.setSelectUserList(submitAnwserYn);
						dtl.setSelectUserList(userList);
						dtl.setSelectUserPercent(form.format(selectPercent));
						dtlList.add(dtl);
					}
					/* 응시 여부 처리 */
					if(answerCnt > 0) {
						submitAnwserYn  = "Y";
					}
					
					info.setQuizDtlList(dtlList);
					info.setSubmitAnswerYn(submitAnwserYn);
					info.setMySubmitAnwer(mySubmitAnswer);
					infoList.add(info);
				}
				quizDetailResponse.setQuizInfoList(infoList);
			
			
			}else {
				throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
			}
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[quizDelete] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[quizDelete] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizDetailResponseDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		}catch (Exception e) {
			logger.info("[quizDelete] ERROR_CODE=000003, ERROR_MSG=" + e.getMessage());
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizDetailResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(QuizDetailResponseDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
    					.quizNo(quizDetailResponse.getQuizNo())
    					.title(quizDetailResponse.getTitle())
    					.startDt(quizDetailResponse.getStartDt())
    					.status(quizDetailResponse.getStatus())
    					.runningTime(quizDetailResponse.getRunningTime())
    					.quizInfoList(quizDetailResponse.getQuizInfoList())
						.build());
	}

	/* 퀴즈 상태값 변경 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<QuizCommonResponseDto> quizChange(HttpServletRequest request, QuizChangeRequestDto quizChangeRequestDto) {

		try {
			
			logger.info("[quizStart] quizStartRequestDto=" + quizChangeRequestDto.toString());
			
			/* 파라미터 검증 */
			if (
					StringUtil.isEmpty(quizChangeRequestDto.getRoomCode())
					|| StringUtil.isEmpty(quizChangeRequestDto.getRoomToken())
					|| StringUtil.isEmpty(quizChangeRequestDto.getUserId())
					|| StringUtil.isEmpty(quizChangeRequestDto.getUserNm())
					|| StringUtil.isEmpty(quizChangeRequestDto.getQuizChange())
			) {
	    		throw new ApiException("000001", commonConstant.M_000001);
			}
			
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
	    	dbMap.clear();
	    	dbMap.put("roomToken", quizChangeRequestDto.getRoomToken());
	    	dbMap.put("roomCode", quizChangeRequestDto.getRoomCode());
	    	dbMap.put("userId", quizChangeRequestDto.getUserId());
	    	dbMap.put("userNm", quizChangeRequestDto.getUserNm());
	    	RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
        	if("000000".equals(roomTokenInfo.getResultCode())) {
        		/* 로그용 */
        		quizChangeRequestDto.setSvcCode(roomTokenInfo.getSvcCode());

        		dbMap.clear();
				dbMap.put("roomCode", quizChangeRequestDto.getRoomCode());
				dbMap.put("quizNo", quizChangeRequestDto.getQuizNo());
				dbMap.put("uptId", quizChangeRequestDto.getUserId());
				dbMap.put("uptDt", quizChangeRequestDto.getNowTime());
				if("S".equals(quizChangeRequestDto.getQuizChange())) {
					dbMap.put("startDt", quizChangeRequestDto.getNowTime());
					dbMap.put("status", "02");
				}else if("E".equals(quizChangeRequestDto.getQuizChange())) {
					dbMap.put("status", "03");
				}else {
					throw new ApiException("000004", commonConstant.M_000004);
				}
				Boolean checkStart = Util.resultCheck(quizMapper.updateQuizInfo(dbMap));
				
				if(!checkStart) {
					throw new ApiException("005004", commonConstant.M_005004);
				}
			
			}else {
				throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
			}
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[quizStart] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[quizStart] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCommonResponseDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		}catch (Exception e) {
			logger.info("[quizStart] ERROR_CODE=000003, ERROR_MSG=" + e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCommonResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(QuizCommonResponseDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}
	
	/* 정답 제출 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<QuizCommonResponseDto> answerSubmit(HttpServletRequest request, AnswerSubmitRequestDto answerSubmitRequestDto) {

		try {
			
			logger.info("[answerSubmit] answerSubmitRequestDto=" + answerSubmitRequestDto.toString());
			
			/* 파라미터 검증 */
			if (
					StringUtil.isEmpty(answerSubmitRequestDto.getRoomCode())
					|| StringUtil.isEmpty(answerSubmitRequestDto.getRoomToken())
					|| StringUtil.isEmpty(answerSubmitRequestDto.getUserId())
					|| StringUtil.isEmpty(answerSubmitRequestDto.getUserNm())
			) {
	    		throw new ApiException("000001", commonConstant.M_000001);
			}
			
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
	    	dbMap.clear();
	    	dbMap.put("roomToken", answerSubmitRequestDto.getRoomToken());
	    	dbMap.put("roomCode", answerSubmitRequestDto.getRoomCode());
	    	dbMap.put("userId", answerSubmitRequestDto.getUserId());
	    	dbMap.put("userNm", answerSubmitRequestDto.getUserNm());
	    	RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenInfo(request, dbMap);
        	if("000000".equals(roomTokenInfo.getResultCode())) {
        		/* 로그용 */
        		answerSubmitRequestDto.setSvcCode(roomTokenInfo.getSvcCode());
			
				for(int i = 0; i < answerSubmitRequestDto.getQuizAnswerList().size(); i++) {
					dbMap.clear();
					dbMap.put("quizNo", answerSubmitRequestDto.getQuizNo());
					dbMap.put("quizSubNo", answerSubmitRequestDto.getQuizAnswerList().get(i).getQuizSubNo());
					dbMap.put("userId", answerSubmitRequestDto.getUserId());
					dbMap.put("userNm", answerSubmitRequestDto.getUserNm());
					dbMap.put("userStatus", answerSubmitRequestDto.getQuizAnswerList().get(i).getUserStatus());
					dbMap.put("submitAnswer", answerSubmitRequestDto.getQuizAnswerList().get(i).getSubmitAnswer());
					
					Boolean checkQuizUser = Util.resultCheck(quizMapper.insertQuizUser(dbMap));
					
					if(!checkQuizUser) {
						throw new ApiException("005005", commonConstant.M_005005, "TB_QUIZ_USER 저장 실패");
					}
					
				}
			
			}else {
				throw new ApiException(roomTokenInfo.getResultCode(), roomTokenInfo.getResultMsg());
			}
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[answerSubmit] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[answerSubmit] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCommonResponseDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
		}catch (Exception e) {
			logger.info("[answerSubmit] ERROR_CODE=000003, ERROR_MSG=" + e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(QuizCommonResponseDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(QuizCommonResponseDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}


}
