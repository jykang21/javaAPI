package kr.co.softbridge.sobroplatform.login.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;
import kr.co.softbridge.sobroplatform.commons.service.TloLogService;
import kr.co.softbridge.sobroplatform.commons.util.MacAddressUtil;
import kr.co.softbridge.sobroplatform.login.dto.LiveUserResponeDao;
import kr.co.softbridge.sobroplatform.login.dto.RoomJoinDto;
import kr.co.softbridge.sobroplatform.login.dto.TokenDto;
import kr.co.softbridge.sobroplatform.login.service.LoginService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 로그인 controller
 */
/**
 * <pre>
 * sobroplatform
 * LoginController.java
 * </pre>
 * 
 * @Author	: sb.jykang
 * @Date 	: 2021. 6. 8.
 * @Version	: 
 */
@RestController
@RequestMapping("/login")
public class LoginController {
	private static final Logger logger = LogManager.getLogger(LoginController.class);
        
    @Autowired
    private LoginService loginService;

    @Autowired
    private MonitoringLogService monitoringLogService;
    
    @Autowired
    private TloLogService tloLogService;
    
	/**
	 * <pre>
	 * @throws UnsupportedEncodingException 
	 * @throws GeneralSecurityException 
	 * @throws NoSuchAlgorithmException 
	 * @throws URISyntaxException 
	 * @Method Name : login
	 * 1. 개요 : 로그인 처리
	 * 2. 처리내용 : 방송입장가능여부 확인하여 사용자토큰발행한다.
	 * 3. 작성자	: 2021. 6. 8.
	 * 4. 작성일	: sb.jykang
	 * </pre>
	 * 
	 * @Parameter	: HashMap
	 * @ReturnType	: LiveUserDto
	 */
    @ApiOperation(value = "회의방 로그인", notes = "회의 참여를 위한 로그인 처리")
    @PostMapping(value = "/roomJoin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomJoinDto> roomJoin(
    		HttpServletRequest request
    		, @RequestHeader Map<String, Object> requestHeader
    		, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
		HashMap<String, Object> deviceMap = MacAddressUtil.isDevice(request);
		String localDevice = (String) deviceMap.get("os") 
				+ ", " + (String) deviceMap.get("device")
				+ ", " + (String) deviceMap.get("brower");
		paramMap.put("device", localDevice);
		paramMap.put("nowTime", startServerTime);
    	// 서비스 호출
    	ResponseEntity<RoomJoinDto> result = loginService.roomJoin(request, paramMap);

    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 로그인", "R", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010201", result.getBody().getResultCode(), paramMap);
    	return result;
    }
	
	/**
	 * <pre>
	 * @Method Name : logout
	 * 1. 개요 : 사용자방송접속종료
	 * 2. 처리내용 : 사용자가 나가기 했을경우 토큰 만료처리 및 room_user.end_yn을 Y로 변경
	 * 3. 작성자	: 2021. 6. 9.
	 * 4. 작성일	: sb.jykang
	 * </pre>
	 * 
	 * @Parameter	: 
	 * @ReturnType	: 
	 */
	@ApiOperation(value = "회의방 로그아웃", notes = "퇴실을 위한 로그아웃 처리")
    @PostMapping(value = "/roomOut", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LiveUserResponeDao> roomOut(HttpServletRequest request, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	// 서비스 호출
    	paramMap.put("nowTime", startServerTime);
    	ResponseEntity<LiveUserResponeDao> result = loginService.roomOut(request, paramMap);

    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 로그아웃", "D", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010202", result.getBody().getResultCode(), paramMap);
    	return result;
	}
	
	@ApiOperation(value = "서비스 토큰 생성", notes = "서비스 검증 토큰 생성")
    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenDto> auth(HttpServletRequest request, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	// 서비스 호출
    	ResponseEntity<TokenDto> result = loginService.auth((HashMap<String, Object>) paramMap);

    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 로그아웃", "D", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010101", result.getBody().getResultCode(), paramMap);
    	return result;
    }
	
	@ApiOperation(value = "서비스 토큰 검증", notes = "서비스 검증 토큰 검증")
    @PostMapping(value = "/auth/svcVerify", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean svcVerify(HttpServletRequest request, @RequestBody(required = true) Map<String, Object> paramMap) throws Exception {
			
		return loginService.svcVerify(request, paramMap);
    	
    }
	
	@ApiOperation(value = "룸 토큰 검증", notes = "룸 검증 토큰 검증")
    @PostMapping(value = "/auth/roomVerify", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean roomVerify(HttpServletRequest request, @RequestBody(required = true) Map<String, Object> paramMap) throws Exception {
			
		return loginService.roomVerify(request, paramMap);
    	
    }
}