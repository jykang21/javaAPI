package kr.co.softbridge.sobroplatform.commons.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringApiCallLogDto;
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringRealTimeRoomDto;
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringRealTimeRoomResDto;
import kr.co.softbridge.sobroplatform.commons.dto.RoomVerifyResponseDto;
import kr.co.softbridge.sobroplatform.commons.mapper.CommonCodeMapper;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.config.jwt.JwtUtil;

@Service
public class MonitoringLogService {

	private static final Logger logger = LogManager.getLogger(MonitoringLogService.class);

    @Value("${jwt.secret}")
    private String secret; 
    
    @Autowired
    private CommonCodeMapper commonCodeMapper;
    
    @Autowired
    private CommonTokenService commonTokenService;

	public void roomRealTimeLog(HttpServletResponse requestHeader, Map<String, Object> paramMap) {
		JwtUtil jwtUtil = new JwtUtil(secret);
		
		try {
			logger.info("[roomRealTimeLog] paramMap=[" + paramMap.toString() + "]");
			// 토큰검증 로직추가영역
			RoomVerifyResponseDto roomTokenInfo = commonTokenService.getRoomTokenCheck(paramMap);
			if("000000".equals(roomTokenInfo.getResultCode())) {
				// paramMap 필요변수 정의
				/* roomToken 검증추가시 토큰정보로 대체될 변수 start */
				String	svcCode				= roomTokenInfo.getSvcCode();
				String	roomCode			= (String) paramMap.get("roomCode");
				String	userId				= (String) paramMap.get("userId");
				/* roomToken 검증추가시 토큰정보로 대체될 변수 end */
				String	videoBitrate		= StringUtil.null2void(paramMap.get("videoBitrate"), "0bps");
				String	videoWidth			= StringUtil.null2void(paramMap.get("videoWidth"), "0px");
				String	videoHeight			= StringUtil.null2void(paramMap.get("videoHeight"), "0px");
				String	videoFrameRate		= StringUtil.null2void(paramMap.get("videoFrameRate"), "0bps");
				String	videoAspectRatio	= StringUtil.null2void(paramMap.get("videoAspectRatio"), "0");
				String	audioBitrate		= StringUtil.null2void(paramMap.get("audioBitrate"), "0bps");
				String	audioLaency			= StringUtil.null2void(paramMap.get("audioLaency"), "0sec");
				String	audioSampleRate		= StringUtil.null2void(paramMap.get("audioSampleRate"), "0");
				String	audioSampleSize		= StringUtil.null2void(paramMap.get("audioSampleSize"), "0");
				
				JSONObject consoleMsg	= new JSONObject();
				
				consoleMsg.put("svcCode",			svcCode);
				consoleMsg.put("roomCode",			roomCode);
				consoleMsg.put("userId",			userId);
				consoleMsg.put("videoBitrate",		videoBitrate);
				consoleMsg.put("videoWidth",		videoWidth);
				consoleMsg.put("videoHeight",		videoHeight);
				consoleMsg.put("videoFrameRate",	videoFrameRate);
				consoleMsg.put("videoAspectRatio",	videoAspectRatio);
				consoleMsg.put("audioBitrate",		audioBitrate);
				consoleMsg.put("audioLaency",		audioLaency);
				consoleMsg.put("audioSampleRate",	audioSampleRate);
				consoleMsg.put("audioSampleSize",	audioSampleSize);
							
				System.out.println("quality: "+consoleMsg.toString());
			}
		} catch(Exception e) {
			logger.info("[roomRealTimeLog] " + e.getMessage());
			e.printStackTrace();
			//throw new RuntimeException();
		}
	}
	
	public void roomLog(String logName, Object obj) {
		
		try {
			JSONObject jo = StringUtil.objectToJson(obj);
						
			System.out.println(logName + ": " + jo.toString());
		} catch(Exception e) {
			logger.info("[roomLog] " + e.getMessage());
			e.printStackTrace();
			//throw new RuntimeException();
		}
	}
	
	public void apiCallLog(MonitoringApiCallLogDto apiDto) {
		try {
			String apiServiceNo = getApiServiceNo();
			apiDto.setApiServiceNo(apiServiceNo);
			JSONObject jo = StringUtil.objectToJson(apiDto);
						
			System.out.println("apiInfo: " + jo.toString());
		} catch(Exception e) {
			logger.info("[apiCallLog apiDto] " + e.getMessage());
			e.printStackTrace();
			//throw new RuntimeException();
		}
	}
	
	public void apiCallLog(
			String startDate
			, String endDate
			, String apiServiceNm
			, String apiReqServiceCode
			, String reqResultCode
			, HashMap<String, Object> param
			, ResponseEntity result) {
		try {
			MonitoringApiCallLogDto macld = new MonitoringApiCallLogDto();
	    	macld.setApiServiceNm(apiServiceNm);
	    	macld.setApiReqServiceCode(apiReqServiceCode);
	    	macld.setSvcCode((String) param.get("svcCode"));
	    	macld.setSvcNm(commonConstant.SERVICE_NAME);
	    	macld.setReqResultCode(reqResultCode);
	    	macld.setReqTime(startDate);
	    	if (param != null) {
	    		macld.setReqText("request: "+StringUtil.objectToJson(param).toString().replaceAll("\\\"", ""));
	    	}
	    	macld.setResTime(endDate);
	    	if (result != null) {
	    		macld.setResText("response: "+StringUtil.objectToJson(result.getBody()).toString().replaceAll("\\\"", ""));
	    	}
	    	
			String apiServiceNo = getApiServiceNo();
			macld.setApiServiceNo(apiServiceNo);
			JSONObject jo = StringUtil.objectToJson(macld);
						
			System.out.println("apiInfo: " + jo.toString());
		} catch(Exception e) {
			logger.info("[apiCallLog] " + e.getMessage());
			e.printStackTrace();
			//throw new RuntimeException();
		}
	}
	
	public String getApiServiceNo() {
		
		SecureRandom random = new SecureRandom();
		int rdmInt = random.nextInt(9);
		String pin = "";
		String pattern = "YYYYMMDDHHmmssSSS";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	String setTime = simpleDateFormat.format(new Date());
    	while (pin.length() < 2) {
		    rdmInt = random.nextInt(9);
		    String addition = String.valueOf(rdmInt);
		    if (pin.contains(addition)) continue;
		    pin += addition;
		}
		pin = "ASN" + setTime + pin;
		
		return pin;
//		return commonCodeMapper.getApiServiceNo();
	}

	public ResponseEntity<MonitoringRealTimeRoomResDto> getRoomList(Map<String, Object> paramMap) throws URISyntaxException {
		List<MonitoringRealTimeRoomDto> roomList = null;
		int totalCount = 0;
		try {
			
			totalCount = commonCodeMapper.getRealTimeRoomListTotalCount(paramMap);
			
			int pageNum = Integer.parseInt(StringUtil.null2void(paramMap.get("page"), "1"));
			int pageSize = Integer.parseInt(StringUtil.null2void(paramMap.get("pageSize"), "50"));
			if(pageNum == 1) {
				pageNum = pageNum - 1;
			}else {
				pageNum = (pageNum -1) * pageSize;
			}
			paramMap.put("pageNum", pageNum);
			paramMap.put("pageSize", pageSize);
			roomList = commonCodeMapper.getRealTimeRoomList(paramMap);
		}catch (Exception e) {
			logger.info("000003", commonConstant.M_000003, e.getMessage());
			return ResponseEntity.status(HttpStatus.OK)
					.body(MonitoringRealTimeRoomResDto
							.builder()
							.resultCode("000003")
							.resultMsg(commonConstant.M_000003)
							.build());
		}
		return ResponseEntity.created(new URI("/realTimeRoomList"))
				.body(MonitoringRealTimeRoomResDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.roomCnt(totalCount)
						.roomList(roomList)
						.build());
	}
	
}
