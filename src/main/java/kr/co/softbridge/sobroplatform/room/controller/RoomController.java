package kr.co.softbridge.sobroplatform.room.controller;

import kr.co.softbridge.sobroplatform.commons.dto.CommonResDto;
import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;
import kr.co.softbridge.sobroplatform.commons.service.TloLogService;
import kr.co.softbridge.sobroplatform.commons.util.MacAddressUtil;
import kr.co.softbridge.sobroplatform.login.dto.FastStartDto;
import kr.co.softbridge.sobroplatform.room.dto.RoomListDto;
import kr.co.softbridge.sobroplatform.room.dto.RoomResponeDao;
import kr.co.softbridge.sobroplatform.room.service.RoomService;
import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RequiredArgsConstructor
@RequestMapping("/room")
@RestController
public class RoomController {

	@Value("${platform.aes256.key}")
    private String key;
	
    private final RoomService roomService;
    
    private final MonitoringLogService monitoringLogService;
    
    @Autowired
    private TloLogService tloLogService;

    @ApiOperation(value = "room 목록", notes = "room 목록을 조회")
    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomListDto> roomList(HttpServletRequest request, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	// 서비스 호출
    	ResponseEntity<RoomListDto> result = roomService.RoomList(request, paramMap, key);

    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 목록 조회", "R", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010301", result.getBody().getResultCode(), paramMap);
		return result;
    }
    
    /**
     * <pre>
     * @Method Name : roomCreate
     * 1. 개요 : 방송생성
     * 2. 처리내용 : 회의를 위한 정보를 등록한다.
     * 3. 작성자	: 2021. 7. 5.
     * 4. 작성일	: sb.jykang
     * </pre>
     * 
     * @Parameter	: JSON Map
     * @ReturnType	: ResponseEntity<ScheduleResponeDao>
     */
    @ApiOperation(value = "room 생성", notes = "room을 생성")
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomResponeDao> roomCreate(HttpServletRequest request, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	paramMap.put("nowTime", startServerTime);
    	// 서비스 호출
    	ResponseEntity<RoomResponeDao> result = roomService.createRoom(request, paramMap, key);
    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 생성", "C", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010302", result.getBody().getResultCode(), paramMap);
    	return result;
    }
    
    @ApiOperation(value = "room 수정", notes = "room정보 수정")
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomResponeDao> roomUpdate(HttpServletRequest request, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	paramMap.put("nowTime", startServerTime);
    	
    	// 서비스 호출
    	ResponseEntity<RoomResponeDao> result = roomService.updateRoom(request, paramMap, key);

    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 정보 수정", "U", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010303", result.getBody().getResultCode(), paramMap);
		return result;
    }
    
    @ApiOperation(value = "room 삭제", notes = "room정보 삭제")
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomResponeDao> roomDelete(HttpServletRequest request, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	paramMap.put("nowTime", startServerTime);
    	
    	// 서비스 호출
    	ResponseEntity<RoomResponeDao> result = roomService.deleteRoom(request, paramMap, key);

    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 정보 삭제", "D", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010304", result.getBody().getResultCode(), paramMap);
    	return result;
    }
    
    @ApiOperation(value = "바로 시작", notes = "회의를 바로 시작")
    @PostMapping(value = "/fastStart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FastStartDto> fastStart(
    		HttpServletRequest request
    		, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
    	HashMap<String, Object> deviceMap = MacAddressUtil.isDevice(request);
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	String localDevice = (String) deviceMap.get("os") 
				+ ", " + (String) deviceMap.get("device")
				+ ", " + (String) deviceMap.get("brower");
		paramMap.put("device", localDevice);
		paramMap.put("nowTime", startServerTime);
    	// 서비스 호출
    	ResponseEntity<FastStartDto> result = roomService.fastStart(request, paramMap, key);

    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 바로 시작", "C", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010305", result.getBody().getResultCode(), paramMap);
    	return result;
    }
    
    @ApiOperation(value = "회의 재시작", notes = "회의를 상태값을 진행중으로 변경")
    @PostMapping(value = "/reStartStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResDto> reStartStatus(
    		HttpServletRequest request
    		, @RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
    	HashMap<String, Object> deviceMap = MacAddressUtil.isDevice(request);
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	String localDevice = (String) deviceMap.get("os") 
				+ ", " + (String) deviceMap.get("device")
				+ ", " + (String) deviceMap.get("brower");
		paramMap.put("device", localDevice);
    	
    	// 서비스 호출
    	ResponseEntity<CommonResDto> result = roomService.reStartStatus(request, paramMap, key);

    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "방송 재시작 상태변경", "C", result.getBody().getResultCode(), paramMap, result);
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010306", result.getBody().getResultCode(), paramMap);
    	return result;
    }
    
}
