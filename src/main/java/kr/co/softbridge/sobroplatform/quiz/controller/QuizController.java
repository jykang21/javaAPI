package kr.co.softbridge.sobroplatform.quiz.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.softbridge.sobroplatform.quiz.dto.QuizCreateRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizCreateResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDeleteRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDetailRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDetailResponseDto;
import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;
import kr.co.softbridge.sobroplatform.commons.service.TloLogService;
import kr.co.softbridge.sobroplatform.quiz.dto.AnswerSubmitRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizChangeRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizCommonResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizListRequestDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizListResponseDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizUpdateRequestDto;
import kr.co.softbridge.sobroplatform.quiz.service.QuizService;

@RestController
@RequestMapping("/quiz")
public class QuizController {
	
	private static final Logger logger = LogManager.getLogger(QuizController.class);
	
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private MonitoringLogService monitoringLogService;
    
    @Autowired
    private TloLogService tloLogService;
	
    /* Quiz Create */
	@PostMapping(value = "/quizCreate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QuizCreateResponseDto> quizCreate(HttpServletRequest request, @RequestBody QuizCreateRequestDto quizCreateRequestDto) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	quizCreateRequestDto.setNowTime(startServerTime);
    	
		ResponseEntity<QuizCreateResponseDto> result = quizService.quizCreate(request, quizCreateRequestDto);
		
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	HashMap<String, Object> logMap = new HashMap<String, Object>();
    	logMap.put("svcCode", quizCreateRequestDto.getSvcCode());
    	logMap.put("roomCode", quizCreateRequestDto.getRoomCode());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "퀴즈 생성", "C", result.getBody().getResultCode(), logMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010702", result.getBody().getResultCode(), logMap);
		
		return result;
	}
	
	/* Quiz List */
	@PostMapping(value = "/quizList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QuizListResponseDto> quizList(HttpServletRequest request, @RequestBody QuizListRequestDto quizListRequestDto) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
		ResponseEntity<QuizListResponseDto> result = quizService.quizList(request, quizListRequestDto);
		
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	HashMap<String, Object> logMap = new HashMap<String, Object>();
    	logMap.put("svcCode", quizListRequestDto.getSvcCode());
    	logMap.put("roomCode", quizListRequestDto.getRoomCode());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "퀴즈 목록", "R", result.getBody().getResultCode(), logMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010701", result.getBody().getResultCode(), logMap);
    	
		return result;
	}
	
	/* Quiz Update */
	@PostMapping(value = "/quizUpdate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QuizCommonResponseDto> quizUpdate(HttpServletRequest request, @RequestBody QuizUpdateRequestDto quizUpdateRequestDto) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	quizUpdateRequestDto.setNowTime(startServerTime);
    	
		ResponseEntity<QuizCommonResponseDto> result = quizService.quizUpdate(request, quizUpdateRequestDto);
		
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	HashMap<String, Object> logMap = new HashMap<String, Object>();
    	logMap.put("svcCode", quizUpdateRequestDto.getSvcCode());
    	logMap.put("roomCode", quizUpdateRequestDto.getRoomCode());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "퀴즈 수정", "DC", result.getBody().getResultCode(), logMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010703", result.getBody().getResultCode(), logMap);
		
		return result;
	}
	
	/* Quiz Delete */
	@PostMapping(value = "/quizDelete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QuizCommonResponseDto> quizDelete(HttpServletRequest request, @RequestBody QuizDeleteRequestDto quizDeleteRequestDto) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	quizDeleteRequestDto.setNowTime(startServerTime);
    	
		ResponseEntity<QuizCommonResponseDto> result = quizService.quizDelete(request, quizDeleteRequestDto);
		
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	HashMap<String, Object> logMap = new HashMap<String, Object>();
    	logMap.put("svcCode", quizDeleteRequestDto.getSvcCode());
    	logMap.put("roomCode", quizDeleteRequestDto.getRoomCode());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "퀴즈 삭제", "U", result.getBody().getResultCode(), logMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010704", result.getBody().getResultCode(), logMap);
		
		return result;
	}
	
	/* Quiz Detail */
	@PostMapping(value = "/quizDetail", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QuizDetailResponseDto> quizDetail(HttpServletRequest request, @RequestBody QuizDetailRequestDto quizDetailRequestDto) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
		ResponseEntity<QuizDetailResponseDto> result = quizService.quizDetail(request, quizDetailRequestDto);
		
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	HashMap<String, Object> logMap = new HashMap<String, Object>();
    	logMap.put("svcCode", quizDetailRequestDto.getSvcCode());
    	logMap.put("roomCode", quizDetailRequestDto.getRoomCode());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "퀴즈 상세", "R", result.getBody().getResultCode(), logMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010705", result.getBody().getResultCode(), logMap);
		
		return result;
	}
	
	/* Quiz Start */
	@PostMapping(value = "/quizChange", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QuizCommonResponseDto> quizChange(HttpServletRequest request, @RequestBody QuizChangeRequestDto quizChangeRequestDto) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	quizChangeRequestDto.setNowTime(startServerTime);
    	
		ResponseEntity<QuizCommonResponseDto> result = quizService.quizChange(request, quizChangeRequestDto);
		
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	HashMap<String, Object> logMap = new HashMap<String, Object>();
    	logMap.put("svcCode", quizChangeRequestDto.getSvcCode());
    	logMap.put("roomCode", quizChangeRequestDto.getRoomCode());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "퀴즈 상태 변경", "U", result.getBody().getResultCode(), logMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010706", result.getBody().getResultCode(), logMap);
		
		return result;
	}
	
	/* Answer Submit */
	@PostMapping(value = "/answerSubmit", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QuizCommonResponseDto> answerSubmit(HttpServletRequest request, @RequestBody AnswerSubmitRequestDto answerSubmitRequestDto) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
		ResponseEntity<QuizCommonResponseDto> result = quizService.answerSubmit(request, answerSubmitRequestDto);
		
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	HashMap<String, Object> logMap = new HashMap<String, Object>();
    	logMap.put("svcCode", answerSubmitRequestDto.getSvcCode());
    	logMap.put("roomCode", answerSubmitRequestDto.getRoomCode());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "정답 제출", "C", result.getBody().getResultCode(), logMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010707", result.getBody().getResultCode(), logMap);
		
		return result;
	}

}
