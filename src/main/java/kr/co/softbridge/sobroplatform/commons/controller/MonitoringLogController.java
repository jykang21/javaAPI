package kr.co.softbridge.sobroplatform.commons.controller;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringRealTimeRoomResDto;
import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;
import kr.co.softbridge.sobroplatform.commons.service.TloLogService;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/common")
@RestController
public class MonitoringLogController {

	@Autowired
    private MonitoringLogService monitoringLogService;
	
	@Autowired
    private TloLogService tloLogService;

	@ApiOperation(value = "실시간 채널", notes = "방송 모니터링")
	@PostMapping(value = "/realTimeLog", produces = MediaType.APPLICATION_JSON_VALUE)
	public void realTimeLog(
			HttpServletResponse requestHeader
			, @RequestBody(required = true) HashMap<String, Object> paramMap
			) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	String startServerTime = simpleDateFormat.format(new Date());
    	// 서비스 호출
    	monitoringLogService.roomRealTimeLog(requestHeader, paramMap);

    	String endServerTime = simpleDateFormat.format(new Date());
    	
    	//monitoringLogService.apiCallLog(startServerTime, endServerTime, "실시간 방송사용자 미디어LOG", "R", null, paramMap, null);
	}
	
	@ApiOperation(value = "실시간 채널", notes = "방송 모니터링")
	@PostMapping(value = "/realTimeRoomList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MonitoringRealTimeRoomResDto> realTimeRoomList(
			HttpServletRequest request
			, @RequestBody(required = true) HashMap<String, Object> paramMap
			) throws URISyntaxException {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	String startServerTime = simpleDateFormat.format(new Date());
    	
    	paramMap.put("svcCode", StringUtil.null2void(paramMap.get("svcCode"), startServerTime));
    	paramMap.put("nowTime", startServerTime);
    	
    	// 서비스 호출
    	ResponseEntity<MonitoringRealTimeRoomResDto> result = monitoringLogService.getRoomList(paramMap);

    	String endServerTime = simpleDateFormat.format(new Date());
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "실시간 방송목록", "R", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime, endServerTime, "S010501", result.getBody().getResultCode(), paramMap);
    	return result;
	}
}
