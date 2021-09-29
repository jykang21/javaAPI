package kr.co.softbridge.sobroplatform.login.service;

import java.net.URI;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringRoomUserLogDto;
import kr.co.softbridge.sobroplatform.commons.dto.RoomDto;
import kr.co.softbridge.sobroplatform.commons.dto.SobroAuthDto;
import kr.co.softbridge.sobroplatform.commons.dto.SvcInfoDto;
import kr.co.softbridge.sobroplatform.commons.dto.SvcTokenInfoResponseDto;
import kr.co.softbridge.sobroplatform.commons.dto.TokenResDto;
import kr.co.softbridge.sobroplatform.commons.exception.ApiException;
import kr.co.softbridge.sobroplatform.commons.mapper.CommonTokenMapper;
import kr.co.softbridge.sobroplatform.commons.service.CommonTokenService;
import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;
import kr.co.softbridge.sobroplatform.commons.util.MacAddressUtil;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.commons.util.Util;
import kr.co.softbridge.sobroplatform.config.jwt.JwtUtil;
import kr.co.softbridge.sobroplatform.file.service.FileDeleteService;
import kr.co.softbridge.sobroplatform.login.dto.LiveUserDto;
import kr.co.softbridge.sobroplatform.login.dto.LiveUserResponeDao;
import kr.co.softbridge.sobroplatform.login.dto.RoomJoinDto;
import kr.co.softbridge.sobroplatform.login.dto.RoomTokenDto;
import kr.co.softbridge.sobroplatform.login.dto.SvcTokenDto;
import kr.co.softbridge.sobroplatform.login.dto.TokenDto;
import kr.co.softbridge.sobroplatform.login.mapper.LoginMapper;
import kr.co.softbridge.sobroplatform.room.mapper.RoomMapper;
import kr.co.softbridge.sobroplatform.security.AES256Util;

@Service
@Transactional
public class LoginService {

	private static final Logger logger = LogManager.getLogger(LoginService.class);
	
//    @Value("${platform.aes256.key}")
//    private String key; 
    
    @Value("${jwt.secret}")
    private String secret; 
    
    @Value("${jwt.type.BRO}")
    private String BRO; 
    
    @Value("${jwt.type.USER}")
    private String USER; 

	@Autowired
    private LoginMapper loginMapper;
	
	@Autowired
    private RoomMapper roomMapper;
	
	@Autowired
    private CommonTokenService commonTokenService;
	
	@Autowired
    private FileDeleteService fileDeleteService;
	
	@Autowired
	private CommonTokenMapper commonTokenMapper;
	
	@Autowired
	private MonitoringLogService monitoringLogService;
	
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<RoomJoinDto> roomJoin(HttpServletRequest request, Map<String, Object> paramMap) throws Exception {
		LiveUserDto liveUser = new LiveUserDto();
		HashMap<String, Object> checkMap = new HashMap<String, Object>();
		
		try {
			logger.info("[roomJoin] paramMap=" + paramMap.toString()); 
			/*Parameter 검증*/
			if (
					StringUtil.isEmpty(paramMap.get("roomCode")) 
					|| StringUtil.isEmpty(paramMap.get("userNm")) 
					|| StringUtil.isEmpty(paramMap.get("roomPw"))
//					|| StringUtil.isEmpty((String) paramMap.get("userAuthCode"))
			) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/*방 정보 유효성 검증*/
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
			String userAuthCode	= StringUtil.null2void(paramMap.get("userAuthCode"), "U");
			String roomPw 	= StringUtil.null2void(paramMap.get("roomPw"));
			
			if("U".equals(userAuthCode)) {
				/* userNm 글자수 제한(한글 2byte 계산을 위해 ecu-kr 사용 */
				int nmLength = Util.getByteLength(StringUtil.null2void(paramMap.get("userNm"), ""), "euc-kr");
				
				if(nmLength < 2 || nmLength > 20) {
					throw new ApiException("001013", commonConstant.M_001013);
				}
			}
			
			dbMap.clear();
			dbMap.put("roomCode", paramMap.get("roomCode"));
			//dbMap.put("password", ase256.encrypt(roomPw));
			dbMap.put("type", "roomIn");
			dbMap.put("nowTime", paramMap.get("nowTime"));
			RoomDto roomData = loginMapper.getRoom(dbMap);
			
			if(roomData == null) {
				throw new ApiException("001001", commonConstant.M_001001);
			} else if(commonConstant.ROOM_STATUS_CLOSE.equals(roomData.getRoomStatus())) {
				throw new ApiException("001011", commonConstant.M_001011);
			} else if("Y".equals(roomData.getStartArrival())) {
				throw new ApiException("001010", commonConstant.M_001010);
			}
			
			/*닉네임 중복 확인*/
			dbMap.clear();
			dbMap.put("roomCode", paramMap.get("roomCode"));
			dbMap.put("userNm", paramMap.get("userNm"));
			dbMap.put("type", "roomIn");
			liveUser = loginMapper.checkRoomUser(dbMap);

			if (liveUser != null) {
				if(!StringUtil.null2void(paramMap.get("userId"), "").equals(liveUser.getUserId())) {
					throw new ApiException("001005", commonConstant.M_001005);
				}
			}
			
			dbMap.clear();
			dbMap.put("roomCode", paramMap.get("roomCode"));
			dbMap.put("nowTime", paramMap.get("nowTime"));
			int roomPeople = loginMapper.roomPeople(dbMap);
			
			if(roomPeople >= roomData.getMaxPeople()) {
				throw new ApiException("001002", commonConstant.M_001002);
			}
			
			/* 통합로그를 위해 paramMap에 담음 */
			paramMap.put("svcCode", roomData.getSvcCode());
			AES256Util ase256 = new AES256Util(roomData.getSvcCode());
			
			if(roomData.getMemberId().equals(StringUtil.null2void(paramMap.get("userId"), ""))) {
				/* 사용자 권한 */
				paramMap.put("userLevel", commonConstant.LEVEL_MANAGER);
				/* 방생성자 ID와 유저ID 같을 경우 암호화 처리 없이 진행 */
			}else {
				/* 사용자 권한 */
				paramMap.put("userLevel", commonConstant.LEVEL_USER);
				roomPw = ase256.encrypt(roomPw);
			}
			
			/* 방 비밀번호 확인 */
			if(!roomPw.equals(roomData.getRoomPw())) {
				throw new ApiException("001009", commonConstant.M_001009);
			}
			
			/*방송참여등록 및 참여정보 조회*/
			dbMap.clear();
			dbMap.put("roomCode", paramMap.get("roomCode"));
			dbMap.put("userNm", paramMap.get("userNm"));
			dbMap.put("userLevel", paramMap.get("userLevel"));
			dbMap.put("svcCode", roomData.getSvcCode());
			dbMap.put("userId", paramMap.get("userId"));
			dbMap.put("nowTime", paramMap.get("nowTime"));
			liveUser = getRoomUserInfo(dbMap);

			if (liveUser == null) {
				throw new ApiException("001003", commonConstant.M_001003);
			}
			
			// 토큰생성전 방송의 최대참여자수를 추가해준다.
			if (roomData.getMaxPeople() > 0) {
				liveUser.setMaxPeople(roomData.getMaxPeople());
			}
			
			// 방 생성자 ID
//			liveUser.setManagerId(roomData.getMemberId());
			
			// 방 퀄리티
			liveUser.setQuality(roomData.getQuality());
			
			SecureRandom random = new SecureRandom();
			int rdmInt = random.nextInt(9);
			String pin = "";
			String pattern = "HHmmssSSS";
	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	    	String setTime = simpleDateFormat.format(new Date());
	    	while (pin.length() < 8) {
			    rdmInt = random.nextInt(9);
			    String addition = String.valueOf(rdmInt);
			    if (pin.contains(addition)) continue;
			    pin += addition;
			}
			pin = setTime + pin;
			
			/* 서비스 토큰 생성 및 검증 */
			if(StringUtil.isNotEmpty(paramMap.get("svcToken"))) {
				/* 검증 */
				dbMap.clear();
				dbMap.put("svcToken", paramMap.get("svcToken"));
				dbMap.put("userId", paramMap.get("userId"));
				SvcTokenInfoResponseDto svcTokenInfo = commonTokenService.getSvcTokenInfo(request, dbMap);
				if("000000".equals(svcTokenInfo.getResultCode())) {
					checkMap.put("svcTokenIdx", svcTokenInfo.getScvTokenIdx());
					checkMap.put("svcToken", svcTokenInfo.getSvcToken());
				}else {
	    			throw new ApiException(svcTokenInfo.getResultCode(), svcTokenInfo.getResultMsg());
	    		}
			}else {
				/* 생성 */
				/* 서비스 정보 조회 */
				dbMap.clear();
				dbMap.put("svcCode", roomData.getSvcCode());
				dbMap.put("userId", liveUser.getUserId());
				dbMap.put("pin", pin);
				TokenDto svcToken = commonTokenService.svcTokenMake(request, dbMap);
				if("000000".equals(svcToken.getResultCode())) {
					checkMap.put("svcTokenIdx", svcToken.getSvcTokenIdx());
					checkMap.put("svcToken", svcToken.getTokenInfo());
				}else {
	    			throw new ApiException(svcToken.getResultCode(), svcToken.getResultMsg());
	    		}
				
			}
			
			/*룸 토큰 생성 및 저장*/
			JwtUtil jwtUtil = new JwtUtil(secret);
			RoomTokenDto roomToken = new RoomTokenDto();
			roomToken.setPin(pin);
			roomToken.setRoomCode(liveUser.getRoomCode());
			roomToken.setSvcCode(liveUser.getSvcCode());
			roomToken.setUserId(liveUser.getUserId());
			roomToken.setUserNm(liveUser.getUserNm());
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
    		Date d2 = new Date();
    		try {
    		    d2 = format.parse(StringUtil.null2void(roomData.getEndDt(), ""));
    		} catch (ParseException e) {
    		    e.printStackTrace();
    		}
			
    		TokenResDto tokenRes = jwtUtil.createToken(roomToken, BRO, "R", d2);
			
			/* liveUser svcToken 정보 저장 - roomToken의 길이가 너무 길어지는것을 방지*/
			liveUser.setSvcToken(StringUtil.null2void(checkMap.get("svcToken")));
			/* 화면공유 유형 추가 */
			liveUser.setScreenType(roomData.getScreenType());
			
			dbMap.clear();
			dbMap.put("siteCode", roomData.getSvcCode());
			dbMap.put("token", tokenRes.getToken());
			dbMap.put("expDate", tokenRes.getExpDate());
			Boolean insertToken = resultCheck(commonTokenMapper.insertToken(dbMap));
			
			if(insertToken) {
				
				SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
				String tokenIdx = sobroAuth.getTokenIdx();
				logger.info("[tokenIdx] ==== " + tokenIdx);
				
				dbMap.clear();
				dbMap.put("svcTokenIdx", checkMap.get("svcTokenIdx"));
				dbMap.put("svcCode", roomData.getSvcCode());
				dbMap.put("roomTokenIdx", tokenIdx);
				Boolean updateSvcAuth = resultCheck(commonTokenMapper.updateSvcAuth(dbMap));
				if(updateSvcAuth) {
					/*참여자 정보에 토큰정보 업데이트*/
					dbMap.clear();
					dbMap.put("tokenIdx", tokenIdx);
					dbMap.put("userId", liveUser.getUserId());
					dbMap.put("svcCode", roomData.getSvcCode());
					dbMap.put("roomCode", liveUser.getRoomCode());
					Boolean updateLiveUser = resultCheck(loginMapper.updateRoomUserTokenIdx(dbMap));
					if(updateLiveUser) {
						liveUser.setRoomToken(tokenRes.getToken());
					}else {
						/*토큰 정보 업데이트 실패*/
						throw new ApiException("002002", commonConstant.M_002002);
					}
				}else {
					throw new ApiException("002002", commonConstant.M_002002);
				}
			}else {
				/*토큰 생성 실패*/
				throw new ApiException("002001", commonConstant.M_002001);
			}
			
			/*room 생성자와 사용자가 같을 경우 방송중으로 업데이트 처리*/
			if(roomData.getMemberId().equals(liveUser.getUserId())) {
				dbMap.clear();
				dbMap.put("svcCode", roomData.getSvcCode());
				dbMap.put("roomCode", paramMap.get("roomCode"));
				dbMap.put("udtId", paramMap.get("userId"));
				dbMap.put("roomStatus", "02");
				dbMap.put("nowTime", paramMap.get("nowTime"));
				Boolean updateRoom = resultCheck(roomMapper.updateRoom(dbMap));
				if(!updateRoom) {
					throw new ApiException("001008", commonConstant.M_001008, "방 수정에 실패!");
				}
			}
    		
    		// 방송입장 모니터링 로그작업 시작
    		MonitoringRoomUserLogDto mruld = new MonitoringRoomUserLogDto();
    		mruld.setSvcCode(liveUser.getSvcCode());
    		mruld.setUserId(liveUser.getUserId());
    		mruld.setUserNm(liveUser.getUserNm());
    		mruld.setMacAddress(MacAddressUtil.getLocalMacAddress());
    		mruld.setDeviceId((String) paramMap.get("device"));
    		mruld.setRoomCode(liveUser.getRoomCode());
    		monitoringLogService.roomLog("roomUser", mruld);
    		// 방송입장 모니터링 로그작업 종료
			
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[roomJoin] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[roomJoin] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
					.body(RoomJoinDto
							.builder()
							.resultCode(e.getErrorCode())
							.resultMsg(e.getMessage())
							.build());
		}catch (Exception e) {
			logger.info("000003", commonConstant.M_000003, e.getMessage());
			return ResponseEntity.status(HttpStatus.OK)
					.body(RoomJoinDto
							.builder()
							.resultCode("000003")
							.resultMsg(commonConstant.M_000003)
							.build());
		}
		
		return ResponseEntity.created(new URI("/login"))
				.body(RoomJoinDto
						.builder()
						.roomCode(liveUser.getRoomCode())
						.userId(liveUser.getUserId())
						.userNm(liveUser.getUserNm())
						.maxPeople(liveUser.getMaxPeople())
						.userLevel(liveUser.getUserLevel())
						.roomToken(liveUser.getRoomToken())
						.svcToken(liveUser.getSvcToken())
//						.managerId(liveUser.getManagerId())
						.quality(liveUser.getQuality())
						.screenType(liveUser.getScreenType())
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}

	
	public LiveUserDto getRoomUserInfo(Map<String, Object> paramMap) {
		LiveUserDto returnDto = new LiveUserDto();
		Boolean roomUser = false;
		String userId = (String) paramMap.get("userId");
		
		if (userId == null || "".equals(userId)) {
			SecureRandom random = new SecureRandom();
			int rdmInt = random.nextInt(9);
			String pin = "";
			String pattern = "YYYYMMDDHHmmssSSS";
	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	    	String setTime = simpleDateFormat.format(new Date());
	    	while (pin.length() < 4) {
			    rdmInt = random.nextInt(9);
			    String addition = String.valueOf(rdmInt);
			    if (pin.contains(addition)) continue;
			    pin += addition;
			}
			pin = "RU" + setTime + pin;
			
//			userId = loginMapper.getUserId();
			userId = pin;
			paramMap.put("userId", userId);
		}
		
		returnDto = loginMapper.getRoomUser(paramMap);
		
		if(returnDto == null) {
			roomUser = resultCheck(loginMapper.insertRoomUser(paramMap));
		}else {
			paramMap.put("tokenIdx", "1");
			roomUser = resultCheck(loginMapper.updateRoomUserTokenIdx(paramMap));
		}
		
		if(roomUser) {
			returnDto = loginMapper.getRoomUser(paramMap);
		}
		
		return returnDto;
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<LiveUserResponeDao> roomOut(HttpServletRequest request, Map<String, Object> paramMap) throws Exception{
		
		HashMap<String, Object> dbMap = new HashMap<String, Object>();
		
		try {
			/*Parameter 검증*/
			logger.info("[roomOut] paramMap=" + paramMap.toString());
			if (
					StringUtil.isEmpty((String) paramMap.get("roomCode")) 
					|| StringUtil.isEmpty((String) paramMap.get("userId")) 
					|| StringUtil.isEmpty((String) paramMap.get("roomToken"))
			) {
				throw new ApiException("001001", commonConstant.M_001001);
			}
			
			/*사용자 정보 조회*/
			dbMap.clear();
			dbMap.put("roomCode", paramMap.get("roomCode"));
			dbMap.put("userId", paramMap.get("userId"));
			LiveUserDto roomUser = loginMapper.getRoomUser(paramMap);
			if(roomUser == null) {
				throw new ApiException("001006", commonConstant.M_001006);
			}
			/* 통합로그를 위해 paramMap에 담음 */
			paramMap.put("svcCode", roomUser.getSvcCode());
			/*방 정보 조회*/
			dbMap.clear();
			dbMap.put("roomCode", paramMap.get("roomCode"));
			dbMap.put("type", "roomOut");
			dbMap.put("nowTime", paramMap.get("nowTime"));
			RoomDto roomData = loginMapper.getRoom(dbMap);

			if(roomData == null) {
				throw new ApiException("001001", commonConstant.M_001001);
			}
			
			/*토큰 유무 조회*/
			dbMap.clear();
			dbMap.put("token", paramMap.get("roomToken"));
			dbMap.put("tokenIdx", roomUser.getTokenIdx());
			TokenResDto tokenRes = loginMapper.getTokenInfo(dbMap);
			if(tokenRes == null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(LiveUserResponeDao
								.builder()
								.resultCode("002003")
								.resultMsg(commonConstant.M_002003)
								.build());
			}
			
			
			/*room 토큰 검증*/
//			dbMap.clear();
//			dbMap.put("roomToken", paramMap.get("roomToken"));
//			dbMap.put("svcCode", roomUser.getSvcCode());
//			dbMap.put("roomCode", roomUser.getRoomCode());
//			dbMap.put("userId", roomUser.getUserId());
//			dbMap.put("userNm", roomUser.getUserNm());
//			Boolean tokenCheck = commonTokenService.roomVerify(dbMap);
//			if(!tokenCheck) {
//				return ResponseEntity.status(HttpStatus.OK)
//						.body(LiveUserResponeDao
//								.builder()
//								.resultCode("002003")
//								.resultMsg(commonConstant.M_002003)
//								.build());
//			}
			
			
			/*토큰 만료 처리*/
			dbMap.clear();
			dbMap.put("siteCode", roomUser.getSvcCode());
			dbMap.put("tokenIdx", roomUser.getTokenIdx());
			dbMap.put("token", tokenRes.getToken());
			dbMap.put("nowTime", paramMap.get("nowTime"));
			Boolean updateTokenExp = resultCheck(loginMapper.updateTokenExp(dbMap));
//			if(updateTokenExp) {
				/*사용자 만료 처리*/
				dbMap.clear();
				dbMap.put("svcCode", roomUser.getSvcCode());
				dbMap.put("roomCode", paramMap.get("roomCode"));
				dbMap.put("userId", paramMap.get("userId"));
				dbMap.put("nowTime", paramMap.get("nowTime"));
				Boolean updateRoomUser = resultCheck(loginMapper.updateRoomUserEndDate(dbMap));
				if(!updateRoomUser) {
					throw new ApiException("001007", commonConstant.M_001007);
				}
//			}else {
//				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//						.body(LiveUserResponeDao
//								.builder()
//								.resultCode("002002")
//								.resultMsg(commonConstant.M_002002)
//								.build());
//			}
			
			/* 방 사용자가 0명이면 방 종료 처리 및 종료 날짜 세팅 */
			Date now = new Date(System.currentTimeMillis());
			dbMap.clear();
			dbMap.put("svcCode", roomUser.getSvcCode());
			dbMap.put("roomCode", paramMap.get("roomCode"));
			dbMap.put("udtId", paramMap.get("userId"));
			dbMap.put("userId", paramMap.get("userId"));
			dbMap.put("roomStatus", "03");
			dbMap.put("nowTime", paramMap.get("nowTime"));
//				dbMap.put("endDt", now);				
			int roomUserCount = loginMapper.getRoomUserCount(dbMap);
			
			if(roomUserCount == 0) {
				Boolean updateRoom = resultCheck(roomMapper.updateRoom(dbMap));
				if(!updateRoom) {
					throw new ApiException("001004", commonConstant.M_001004);
				}
				
				dbMap.clear();
				dbMap.put("roomCode", paramMap.get("roomCode"));
				fileDeleteService.whiteboardDelete(dbMap);
			}
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[roomOut] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[roomOut] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
					.body(LiveUserResponeDao
							.builder()
							.resultCode(e.getErrorCode())
							.resultMsg(e.getMessage())
							.build());	
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		return ResponseEntity.status(HttpStatus.OK)
				.body(LiveUserResponeDao
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<TokenDto> auth(HashMap<String, Object> paramMap) {
		
		TokenResDto tokenRes = new TokenResDto();
		
		try {
			logger.info("[auth] paramMap=" + paramMap.toString());
			HashMap<String, Object> dbMap = new HashMap<String, Object>();
			/*Paremter 검증*/
			if (
					StringUtil.isEmpty((String) paramMap.get("svcCode")) 
					|| StringUtil.isEmpty((String) paramMap.get("svcDomain")) 
					|| StringUtil.isEmpty((String) paramMap.get("svcNm"))
					|| StringUtil.isEmpty((String) paramMap.get("userId"))
			) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/*parameter와 DB 교차 검증*/
			dbMap.put("svcCode", paramMap.get("svcCode"));
			dbMap.put("svcDomain", paramMap.get("svcDomain"));
			dbMap.put("svcNm", paramMap.get("svcNm"));
			SvcInfoDto svcInfo = loginMapper.getSvcInfo(dbMap);
			if(svcInfo == null) {
				throw new ApiException("002005", commonConstant.M_002005);
			}
			
			SecureRandom random = new SecureRandom();
			int rdmInt = random.nextInt(9);
			String pin = "";
			String pattern = "HHmmssSSS";
	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	    	String setTime = simpleDateFormat.format(new Date());
	    	while (pin.length() < 8) {
			    rdmInt = random.nextInt(9);
			    String addition = String.valueOf(rdmInt);
			    if (pin.contains(addition)) continue;
			    pin += addition;
			}
			pin = setTime + pin;
			
			/*토큰 생성*/
			SvcTokenDto svcToken = new SvcTokenDto();
			svcToken.setSvcCode((String) paramMap.get("svcCode"));
			svcToken.setSvcDomain((String) paramMap.get("svcDomain"));
			svcToken.setPin(pin);
			svcToken.setUserId((String) paramMap.get("userId"));
			//svcToken.setSvcKey((String) paramMap.get("svcNm"));
			
			JwtUtil jwtUtil = new JwtUtil(secret);
			tokenRes = jwtUtil.createToken(svcToken, USER, "S", null);
			
			dbMap.clear();
			dbMap.put("siteCode", paramMap.get("svcCode"));
			dbMap.put("token", tokenRes.getToken());
			dbMap.put("expDate", tokenRes.getExpDate());
			boolean check = resultCheck(commonTokenMapper.insertToken(dbMap));
			
			if(!check) {
				throw new ApiException("002001", commonConstant.M_002001);
			}
			
			SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
			String tokenIdx = sobroAuth.getTokenIdx();
			System.out.println("[tokenIdx] ==== " + tokenIdx);
			
			dbMap.clear();
			dbMap.put("svcTokenIdx", tokenIdx);
			dbMap.put("svcCode", svcInfo.getSvcCode());
			boolean svcCheck = resultCheck(commonTokenMapper.insertSvcAuth(dbMap));
			if(!svcCheck) {
				throw new ApiException("000002", commonConstant.M_000002);
			}
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[auth] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[auth] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
					.body(TokenDto
							.builder()
							.resultCode(e.getErrorCode())
							.resultMsg(e.getMessage())
							.build());
		}catch (Exception e) {
			logger.info("000003", commonConstant.M_000003, e.getMessage());
			return ResponseEntity.status(HttpStatus.OK)
					.body(TokenDto
							.builder()
							.resultCode("000003")
							.resultMsg(commonConstant.M_000003)
							.build());
		}
		
		return ResponseEntity.status(HttpStatus.OK)
				.body(TokenDto
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.tokenInfo(tokenRes.getToken())
						.build());
	}


	public Boolean svcVerify(HttpServletRequest request, Map<String, Object> paramMap) {
		
		Boolean result = false;
		
		try {
			logger.info("[svcVerify] paramMap=" + paramMap.toString());
			result = commonTokenService.svcVerify(request, paramMap);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Boolean roomVerify(HttpServletRequest request, Map<String, Object> paramMap) {
		
		Boolean result = false;
		
		try {
			logger.info("[roomVerify] paramMap=" + paramMap.toString());
			result = commonTokenService.roomVerify(request, paramMap);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public Boolean resultCheck(int Check) {
		
		Boolean result = false;
		
		try {
			if(Check > 0) {
				result = true;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}

}