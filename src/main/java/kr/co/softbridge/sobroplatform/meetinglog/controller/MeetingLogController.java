package kr.co.softbridge.sobroplatform.meetinglog.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;
import kr.co.softbridge.sobroplatform.commons.service.TloLogService;
import kr.co.softbridge.sobroplatform.meetinglog.dto.MeetingLogDto;
import kr.co.softbridge.sobroplatform.meetinglog.dto.MeetingLogRequestDto;
import kr.co.softbridge.sobroplatform.meetinglog.service.MeetingLogService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/meetinglog")
@RestController
public class MeetingLogController {

	@Value("${platform.aes256.key}")
    private String key;
	
	private final MeetingLogService meetingLogService;
    
    private final MonitoringLogService monitoringLogService;
    
    @Autowired
    private TloLogService tloLogService;

    @ApiOperation(value = "회의록 조회", notes = "회의록 내용 조회")
    @PostMapping(value = "/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MeetingLogDto> meetingLogView(HttpServletRequest request, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
    	// 서비스 호출
    	ResponseEntity<MeetingLogDto> result = meetingLogService.selectMeetingLog(request, paramMap);
    	
    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "회의록 내용 조회", "R", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010601", result.getBody().getResultCode(), paramMap);
		return result;
    }
    
    @ApiOperation(value = "회의록 저장", notes = "회의록 내용 저장")
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MeetingLogDto> meetingLogSave(
    		HttpServletRequest request,
    		@RequestBody MeetingLogRequestDto param) throws Exception {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	// 서비스 호출
    	ResponseEntity<MeetingLogDto> result = meetingLogService.saveMeetingLog(request, param);

    	/* 통합로그를 위해 paramMap에 담음 */
    	HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcCode", param.getSvcCode());
		paramMap.put("roomCode", param.getRoomCode());
    	
    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "회의록 내용 저장", "CU", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010602", result.getBody().getResultCode(), paramMap);
		return result;
    }
}
