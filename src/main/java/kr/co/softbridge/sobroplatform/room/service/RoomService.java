package kr.co.softbridge.sobroplatform.room.service;

import java.net.URI;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import kr.co.softbridge.sobroplatform.commons.dto.CommonResDto;
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringRoomLogDto;
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringRoomUserLogDto;
import kr.co.softbridge.sobroplatform.commons.dto.SobroAuthDto;
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
import kr.co.softbridge.sobroplatform.login.dto.FastStartDto;
import kr.co.softbridge.sobroplatform.login.dto.LiveUserDto;
import kr.co.softbridge.sobroplatform.login.dto.RoomTokenDto;
import kr.co.softbridge.sobroplatform.login.mapper.LoginMapper;
import kr.co.softbridge.sobroplatform.login.service.LoginService;
import kr.co.softbridge.sobroplatform.room.dto.RoomCheckDto;
import kr.co.softbridge.sobroplatform.room.dto.RoomDto;
import kr.co.softbridge.sobroplatform.room.dto.RoomListDto;
import kr.co.softbridge.sobroplatform.room.dto.RoomResponeDao;
import kr.co.softbridge.sobroplatform.room.mapper.RoomMapper;
import kr.co.softbridge.sobroplatform.security.AES256Util;

@Service
public class RoomService {
	
	private static final Logger logger = LogManager.getLogger(RoomService.class);
	
    @Value("${jwt.secret}")
    private String secret; 
    
    @Value("${jwt.type.BRO}")
    private String BRO; 

	@Autowired
	private RoomMapper roomMapper;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private LoginMapper loginMapper;

	@Autowired
    private CommonTokenService commonTokenService;
	
	@Autowired
    private CommonTokenMapper commonTokenMapper;
	
	@Autowired
	private MonitoringLogService monitoringLogService;

	public ResponseEntity<RoomListDto> RoomList(HttpServletRequest request, HashMap<String, Object> paramMap, String key) throws Exception {
		
		List<RoomDto> room = null;
		int totalCount = 0;
		
    	try {
    		
    		
    		logger.info("[RoomList] paramMap=" + paramMap.toString());
    		// step1. parameter 검증
    		if(StringUtil.isEmpty(paramMap.get("svcToken"))
    			||StringUtil.isEmpty(paramMap.get("roomType"))
    			|| StringUtil.isEmpty(paramMap.get("userId"))
    		){
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
    		
    		SvcTokenInfoResponseDto svcTokenInfo = commonTokenService.getSvcTokenInfo(request, paramMap);
    		
    		if("000000".equals(svcTokenInfo.getResultCode())) {
    			// step2. room 목록 조회
    			paramMap.put("svcCode", svcTokenInfo.getSvcCode());
    			/* room 목록 총 개수(페이징 처리를 위해) */
    			totalCount = roomMapper.getRoomTotalCount(paramMap);
    			
    			int pageNum = Integer.parseInt(StringUtil.null2void(paramMap.get("page"), "1"));
    			int pageSize = Integer.parseInt(StringUtil.null2void(paramMap.get("pageSize"), "50"));
    			if(pageNum == 1) {
    				pageNum = pageNum - 1;
    			}else {
    				pageNum = (pageNum -1) * pageSize;
    			}
    			paramMap.put("pageNum", pageNum);
    			paramMap.put("pageSize", pageSize);
    			room = roomMapper.getRoomList(paramMap);
//        		if(room.size() > 0) {
//        			for(int i=0; i < room.size(); i++) {
//        				AES256Util aes256 = new AES256Util(key);
//        				String roomPwDec = aes256.decrypt(room.get(i).getRoomPw());
//        				room.get(i).setRoomPw(roomPwDec);
//        			}
//        		}
    		}else {
    			throw new ApiException(svcTokenInfo.getResultCode(), svcTokenInfo.getResultMsg());
    		}
    		
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[RoomList] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[RoomList] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(RoomListDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
    	}catch (Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(RoomListDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		
		// step3. 결과값
    	return ResponseEntity.created(new URI("/list"))
    			.body(RoomListDto
    					.builder()
    					.resultCode("000000")
    					.resultMsg(commonConstant.M_000000)
    					.roomCnt(totalCount)
    					.roomList(room)
    					.build());  
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<RoomResponeDao> createRoom(HttpServletRequest request, HashMap<String, Object> paramMap, String key) {

    	String roomCode = null;
    	
    	try {
    		logger.info("[createRoom] paramMap=" + paramMap.toString());
			//step1. paramter 검증
    		if(
    				StringUtil.isEmpty((String) paramMap.get("svcToken"))
    				||StringUtil.isEmpty((String) paramMap.get("roomType"))
//    				||StringUtil.isEmpty((String) paramMap.get("memberId"))
//    				||StringUtil.isEmpty((String) paramMap.get("memberSeq"))
    				||StringUtil.isEmpty((String) paramMap.get("maxPeople"))
    				||StringUtil.isEmpty((String) paramMap.get("title"))
    				||StringUtil.isEmpty((String) paramMap.get("quality"))
    				||StringUtil.isEmpty((String) paramMap.get("viewYn"))
    				||StringUtil.isEmpty((String) paramMap.get("startDt"))
    				||StringUtil.isEmpty((String) paramMap.get("endDt"))
    				||StringUtil.isEmpty((String) paramMap.get("roomPw"))
//    				||StringUtil.isEmpty((String) paramMap.get("screenType"))
    		) {
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
    		
    		/*서비스 토큰 검증*/
    		SvcTokenInfoResponseDto svcTokenInfo = commonTokenService.getSvcTokenInfo(request, paramMap);
    		if("000000".equals(svcTokenInfo.getResultCode())) {
    			// 방송상태 값이 없으면 초기값 세팅
        		if(StringUtil.isEmpty((String) paramMap.get("roomStatus")))
        			paramMap.put("roomStatus", commonConstant.ROOM_STATUS_WAITING);
        		
        		// 녹화타입 값이 없으면 초기값 세팅
        		if(StringUtil.isEmpty((String) paramMap.get("recType")))
        			paramMap.put("recType", commonConstant.REC_TYPE_PRESENTER_ONLY);
        		
        		if(StringUtil.isEmpty((String) paramMap.get("screenType")))
        			paramMap.put("screenType", "10");
        		
        		/* 방송패스워드 암호화 처리 */
        		AES256Util ase256 = new AES256Util(svcTokenInfo.getSvcCode());
        		String roomPassword = (String) paramMap.get("roomPw");
        		String passwordEnc = ase256.encrypt(roomPassword);
        		paramMap.put("roomPw", passwordEnc);
        		/* 방송패스워드 암호화 처리 종료 */
        		
        		// 신규 room code 발급
        		roomCode = getRoomCode();
        		paramMap.put("roomCode", roomCode);
//        		paramMap.put("regId", paramMap.get("memberId"));
        		paramMap.put("regId", svcTokenInfo.getUserId());
        		paramMap.put("memberId", svcTokenInfo.getUserId());
        		paramMap.put("memberSeq", "0");
        		paramMap.put("svcCode", svcTokenInfo.getSvcCode());
        		
        		//step2. 방 생성
        		int insertCnt = insertRoom(paramMap);
        		
        		if(insertCnt > 0) {
            		// 방생성 모니터링 로그작업 시작
            		MonitoringRoomLogDto mrld = getRoomLogRow(paramMap);
            		mrld.setProgDt(StringUtil.gapDateTime(mrld.getStartDt(), mrld.getEndDt()));
            		mrld.setType("C");
//            		mrld.setHostId(StringUtil.null2void(paramMap.get("memberId")));
            		mrld.setHostId(StringUtil.null2void(svcTokenInfo.getUserId()));
            		monitoringLogService.roomLog("room", mrld);
            		// 방생성 모니터링 로그작업 종료
        		}else {
        			throw new Exception("방 생성에 실패!");
        		}
    		}else {
    			throw new ApiException(svcTokenInfo.getResultCode(), svcTokenInfo.getResultMsg());
    		}
    		
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[createRoom] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[createRoom] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		return ResponseEntity.status(HttpStatus.OK)
					.body(RoomResponeDao
							.builder()
							.resultCode(e.getErrorCode())
                            .resultMsg(e.getMessage())
							.build());
    	}catch (Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(RoomResponeDao
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
    	
    	return ResponseEntity.status(HttpStatus.OK)
				.body(RoomResponeDao
						.builder()
						.roomcode(roomCode)
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<RoomResponeDao> updateRoom(HttpServletRequest request, HashMap<String, Object> paramMap, String key) {
    	
    	try {
    		logger.info("[updateRoom] paramMap=" + paramMap.toString());
			//step1. paramter 검증
    		if(
    				StringUtil.isEmpty((String) paramMap.get("svcToken"))
    				|| StringUtil.isEmpty((String) paramMap.get("roomCode"))
    				|| StringUtil.isEmpty((String) paramMap.get("userId"))
    				|| StringUtil.isEmpty((String) paramMap.get("oldRoomPw"))
    				// || StringUtil.isEmpty((String) paramMap.get("screenType"))
    		) {
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
    		
    		SvcTokenInfoResponseDto svcTokenInfo = commonTokenService.getSvcTokenInfo(request, paramMap);
    		if("000000".equals(svcTokenInfo.getResultCode())) {
    		
	    		if (!StringUtil.isEmpty((String) paramMap.get("roomPw"))) {
	    			/* 방송패스워드 암호화 처리 */
	        		AES256Util ase256 = new AES256Util(svcTokenInfo.getSvcCode());
	        		String roomPassword = (String) paramMap.get("roomPw");
	        		String passwordEnc = ase256.encrypt(roomPassword);
	        		paramMap.put("roomPw", passwordEnc);
	        		/* 방송패스워드 암호화 처리 종료 */
	    		}
	    		
//	    		if (!StringUtil.isEmpty((String) paramMap.get("oldRoomPw"))) {
//	    			/* 검증용 방송패스워드 암호화 처리 */
//	        		AES256Util ase256 = new AES256Util(key);
//	        		String roomPassword = (String) paramMap.get("oldRoomPw");
//	        		String passwordEnc = ase256.encrypt(roomPassword);
//	        		paramMap.put("oldRoomPw", passwordEnc);
//	        		/* 검증용 방송패스워드 암호화 처리 종료 */
//	    		}
	    		
	    		//step2. 수정가능한 사용자인지 검증
//	    		paramMap.put("userId", paramMap.get("udtId"));
	    		paramMap.put("userId", svcTokenInfo.getUserId());
	    		paramMap.put("svcCode", svcTokenInfo.getSvcCode());
	    		int checkRoomUpdateUser = checkUpdateRoomUser(paramMap);
	    		
	    		if(checkRoomUpdateUser == 0) {
	    			throw new ApiException("003001", commonConstant.M_003001);
	    		}
	    		
	    		//step3. 방정보 변경
	    		int updateCnt = updateRoom(paramMap);
	    		
	    		if(updateCnt > 0) {
	        		
	        		// 방수정 모니터링 로그작업 시작
	        		MonitoringRoomLogDto mrld = getRoomLogRow(paramMap);
	        		mrld.setProgDt(StringUtil.gapDateTime(mrld.getStartDt(), mrld.getEndDt()));
	        		mrld.setType("U");
//	        		mrld.setHostId(StringUtil.null2void(paramMap.get("udtId")));
	        		mrld.setHostId(StringUtil.null2void(svcTokenInfo.getUserId()));
	        		monitoringLogService.roomLog("room", mrld);
	        		// 방수정 모니터링 로그작업 종료
	    		}else {
	    			throw new ApiException("001004", commonConstant.M_001004);
	    		}
    		}else {
    			throw new ApiException(svcTokenInfo.getResultCode(), svcTokenInfo.getResultMsg());
    		}
    	
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[updateRoom] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[updateRoom] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		return ResponseEntity.status(HttpStatus.OK)
					.body(RoomResponeDao
							.builder()
							.resultCode(e.getErrorCode())
                            .resultMsg(e.getMessage())
							.build());
    	}catch (Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(RoomResponeDao
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
    	
    	return ResponseEntity.status(HttpStatus.OK)
				.body(RoomResponeDao
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<RoomResponeDao> deleteRoom(HttpServletRequest request, HashMap<String, Object> paramMap, String key) {
    	
    	try {
    		logger.info("[deleteRoom] paramMap=" + paramMap.toString());
    		//step1. paramter 검증
    		if(
    				StringUtil.isEmpty((String) paramMap.get("svcToken"))
    				|| StringUtil.isEmpty((String) paramMap.get("roomCode"))
    				|| StringUtil.isEmpty((String) paramMap.get("userId"))
    				|| StringUtil.isEmpty((String) paramMap.get("roomPw"))
    		) {
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
    		
    		/*서비스 토큰 검증*/
    		SvcTokenInfoResponseDto svcTokenInfo = commonTokenService.getSvcTokenInfo(request, paramMap);
    		if("000000".equals(svcTokenInfo.getResultCode())) {
			
	    		if (!StringUtil.isEmpty((String) paramMap.get("roomPw"))) {
	    			/* 방송패스워드 암호화 처리 */
//	        		AES256Util ase256 = new AES256Util(svcTokenInfo.getSvcCode());
//	        		String roomPassword = (String) paramMap.get("roomPw");
//	        		String passwordEnc = ase256.encrypt(roomPassword);
//	        		paramMap.put("oldRoomPw", passwordEnc);
	        		paramMap.put("oldRoomPw", paramMap.get("roomPw"));
	        		/* 방송패스워드 암호화 처리 종료 */
	    		}
	    		
	    		//step2. 삭제가능한 사용자인지 검증
//	    		paramMap.put("userId", paramMap.get("memberId"));
	    		paramMap.put("userId", svcTokenInfo.getUserId());
	    		paramMap.put("svcCode", svcTokenInfo.getSvcCode());
	    		int checkRoomUpdateUser = checkUpdateRoomUser(paramMap);
	    		
	    		if(checkRoomUpdateUser == 0) {
	    			throw new ApiException("003002", commonConstant.M_003002);
	    		}
	    		
	    		MonitoringRoomLogDto mrld = getRoomLogRow(paramMap);
        		mrld.setProgDt(StringUtil.gapDateTime(mrld.getStartDt(), mrld.getEndDt()));
        		mrld.setType("D");
//        		mrld.setHostId(StringUtil.null2void(paramMap.get("memberId")));
        		mrld.setHostId(StringUtil.null2void(svcTokenInfo.getUserId()));
	    		
	    		//step3. 삭제처리
	    		HashMap<String, Object> delParam = new HashMap<String, Object>();
	    		delParam.put("svcCode", paramMap.get("svcCode"));
	    		delParam.put("roomCode", paramMap.get("roomCode"));
//	    		delParam.put("udtId", paramMap.get("memberId"));
	    		delParam.put("udtId", svcTokenInfo.getUserId());
	    		delParam.put("delYn", "Y");
//	    		int updateCnt = updateRoom(delParam);
	    		int updateCnt = roomMapper.deleteRoom(delParam);
	    		roomMapper.deleteRoomUser(delParam);
	    		
	    		if(updateCnt > 0) {
	        		
	        		// 방삭제 모니터링 로그작업 시작
//	        		MonitoringRoomLogDto mrld = getRoomLogRow(paramMap);
//	        		mrld.setProgDt(StringUtil.gapDateTime(mrld.getStartDt(), mrld.getEndDt()));
//	        		mrld.setType("D");
//	        		mrld.setHostId(StringUtil.null2void(paramMap.get("memberId")));
	        		monitoringLogService.roomLog("room", mrld);
	        		// 방삭제 모니터링 로그작업 종료
	    		}else {
	    			throw new ApiException("001004", commonConstant.M_001004);
	    		}
    		}else {
    			throw new ApiException(svcTokenInfo.getResultCode(), svcTokenInfo.getResultMsg());
    		}
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[deleteRoom] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[deleteRoom] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		return ResponseEntity.status(HttpStatus.OK)
					.body(RoomResponeDao
							.builder()
							.resultCode(e.getErrorCode())
                            .resultMsg(e.getMessage())
							.build());
    	}catch (Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(RoomResponeDao
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
    	
    	return ResponseEntity.status(HttpStatus.OK)
				.body(RoomResponeDao
						.builder()
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());
	}

	public int insertRoom(HashMap<String, Object> paramMap) {
		return roomMapper.insertRoom(paramMap);
	}

	public int updateRoom(HashMap<String, Object> paramMap) {
		return roomMapper.updateRoom(paramMap);
	}

	public String getRoomCode() {
		return roomMapper.getRoomCode();
	}

	public int checkUpdateRoomUser(HashMap<String, Object> paramMap) {
		return roomMapper.checkUpdateRoomUser(paramMap);
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<FastStartDto> fastStart(HttpServletRequest request, HashMap<String, Object> paramMap, String key) throws Exception {
		LiveUserDto liveUser = new LiveUserDto();
		HashMap<String, Object> dbMap = new HashMap<String, Object>();
		try {
			logger.info("[fastStart] paramMap=" + paramMap.toString());
			//step1. paramter 검증
			if(
					StringUtil.isEmpty((String) paramMap.get("svcToken"))
					||StringUtil.isEmpty((String) paramMap.get("roomType"))
//					||StringUtil.isEmpty((String) paramMap.get("memberId"))
//					||StringUtil.isEmpty((String) paramMap.get("memberSeq"))
					||StringUtil.isEmpty((String) paramMap.get("maxPeople"))
					||StringUtil.isEmpty((String) paramMap.get("title"))
					||StringUtil.isEmpty((String) paramMap.get("quality"))
					||StringUtil.isEmpty((String) paramMap.get("viewYn"))
					||StringUtil.isEmpty((String) paramMap.get("startDt"))
					||StringUtil.isEmpty((String) paramMap.get("endDt"))
					||StringUtil.isEmpty((String) paramMap.get("roomPw"))
//					||StringUtil.isEmpty((String) paramMap.get("screenType"))
			) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			SvcTokenInfoResponseDto svcTokenInfo = commonTokenService.getSvcTokenInfo(request, paramMap);
    		if("000000".equals(svcTokenInfo.getResultCode())) {
			
				dbMap.clear();
				dbMap.put("svcCode", svcTokenInfo.getSvcCode());
				dbMap.put("roomType", paramMap.get("roomType"));
//				dbMap.put("memberId", paramMap.get("memberId"));
//				dbMap.put("memberSeq", paramMap.get("memberSeq"));
				dbMap.put("memberId", svcTokenInfo.getUserId());
				dbMap.put("memberSeq", "0");
				dbMap.put("maxPeople", paramMap.get("maxPeople"));
				dbMap.put("title", paramMap.get("title"));
				dbMap.put("quality", paramMap.get("quality"));
				dbMap.put("viewYn", paramMap.get("viewYn"));
				dbMap.put("startDt", paramMap.get("startDt"));
				dbMap.put("endDt", paramMap.get("endDt"));
				dbMap.put("roomPw", paramMap.get("roomPw"));
//				dbMap.put("regId", paramMap.get("regId"));
				dbMap.put("regId", svcTokenInfo.getUserId());
				
				// 방송상태 값이 없으면 초기값 세팅
				if(StringUtil.isEmpty((String) paramMap.get("roomStatus")))
					dbMap.put("roomStatus", commonConstant.ROOM_STATUS_WAITING);
				
				// 녹화타입 값이 없으면 초기값 세팅
				if(StringUtil.isEmpty((String) paramMap.get("recType")))
					dbMap.put("recType", commonConstant.REC_TYPE_PRESENTER_ONLY);
				
				if(StringUtil.isEmpty((String) paramMap.get("screenType"))) {
					dbMap.put("screenType", "10");
				}else {
					dbMap.put("screenType", paramMap.get("screenType"));
				}
				
				/* 방송패스워드 암호화 처리 */
				AES256Util ase256 = new AES256Util(svcTokenInfo.getSvcCode());
				String roomPassword = (String) paramMap.get("roomPw");
				String passwordEnc = ase256.encrypt(roomPassword);
				dbMap.put("roomPw", passwordEnc);
				/* 방송패스워드 암호화 처리 종료 */
				
				// 신규 room code 발급
				String roomCode = getRoomCode();
				dbMap.put("roomCode", roomCode);
				dbMap.put("nowTime", paramMap.get("nowTime"));
				Boolean insertCnt = Util.resultCheck(insertRoom(dbMap));
				
				if(!insertCnt) {
					throw new ApiException("001008", commonConstant.M_001008, "방 생성에 실패!");
				}
				/* 통합로그를 위해 paramMap에 담음 */
    			paramMap.put("svcCode", svcTokenInfo.getSvcCode());
    			paramMap.put("roomCode", roomCode);
	    		// 방생성 모니터링 로그작업 시작
	    		MonitoringRoomLogDto mrld = getRoomLogRow(dbMap);
	    		mrld.setProgDt(StringUtil.gapDateTime(mrld.getStartDt(), mrld.getEndDt()));
	    		mrld.setType("C");
//	    		mrld.setHostId(StringUtil.null2void(paramMap.get("memberId")));
	    		mrld.setHostId(StringUtil.null2void(svcTokenInfo.getUserId()));
	    		monitoringLogService.roomLog("room", mrld);
	    		// 방생성 모니터링 로그작업 종료
				
				// 방입장 값 세팅
				dbMap.clear();
				dbMap.put("roomCode", roomCode);
				dbMap.put("userNm", paramMap.get("userNm"));
				dbMap.put("userLevel", commonConstant.LEVEL_MANAGER);
				dbMap.put("svcCode", svcTokenInfo.getSvcCode());
//				dbMap.put("userId", paramMap.get("memberId"));
				dbMap.put("userId", svcTokenInfo.getUserId());
				dbMap.put("nowTime", paramMap.get("nowTime"));
				liveUser = loginService.getRoomUserInfo(dbMap);
				
				if (liveUser == null) {
					throw new ApiException("001003", commonConstant.M_001003);
				}
				
				// 토큰생성전 방송의 최대참여자수를 추가해준다.
				int maxPeople = Integer.valueOf((String) paramMap.get("maxPeople"));
				String quality = (String) paramMap.get("quality");
				liveUser.setMaxPeople(maxPeople);
				liveUser.setQuality(quality);
				
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
				
				/*토큰 생성 및 저장*/
				JwtUtil jwtUtil = new JwtUtil(secret);
				RoomTokenDto roomToken = new RoomTokenDto();
				roomToken.setPin(pin);
				roomToken.setRoomCode(liveUser.getRoomCode());
				roomToken.setSvcCode(liveUser.getSvcCode());
				roomToken.setUserId(liveUser.getUserId());
				roomToken.setUserNm(liveUser.getUserNm());
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		Date d2 = new Date();
	    		try {
	    		    d2 = format.parse(StringUtil.null2void(paramMap.get("endDt"), ""));
	    		} catch (ParseException e) {
	    		    e.printStackTrace();
	    		}
	    		
				TokenResDto tokenRes = jwtUtil.createToken(roomToken, BRO, "R", d2);
				
				/* 룸 토큰 길이 에러로 여기에 선언 */
				liveUser.setScreenType(StringUtil.null2void(paramMap.get("screenType")));
				
				dbMap.clear();
				dbMap.put("siteCode", svcTokenInfo.getSvcCode());
				dbMap.put("token", tokenRes.getToken());
				dbMap.put("expDate", tokenRes.getExpDate());
				Boolean insertToken = Util.resultCheck(commonTokenMapper.insertToken(dbMap));
				
				if(insertToken) {
					
					SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
					String tokenIdx = sobroAuth.getTokenIdx();
					
					dbMap.clear();
					dbMap.put("svcTokenIdx", svcTokenInfo.getScvTokenIdx());
					dbMap.put("svcCode", svcTokenInfo.getSvcCode());
					dbMap.put("roomTokenIdx", tokenIdx);
					Boolean updateSvcAuth = Util.resultCheck(commonTokenMapper.updateSvcAuth(dbMap));
					if(updateSvcAuth) {
						/*참여자 정보에 토큰정보 업데이트*/
						dbMap.clear();
						dbMap.put("tokenIdx", tokenIdx);
						dbMap.put("userId", liveUser.getUserId());
						dbMap.put("svcCode", svcTokenInfo.getSvcCode());
						dbMap.put("roomCode", liveUser.getRoomCode());
						int updateLiveUser = loginMapper.updateRoomUserTokenIdx(dbMap);
						if(updateLiveUser > 0) {
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
				
				/*방송중으로 업데이트 처리*/
				dbMap.clear();
				dbMap.put("svcCode", svcTokenInfo.getSvcCode());
				dbMap.put("roomCode", roomCode);
//				dbMap.put("udtId", paramMap.get("memberId"));
				dbMap.put("udtId", svcTokenInfo.getUserId());
				dbMap.put("roomStatus", "02");
				dbMap.put("nowTime", paramMap.get("nowTime"));
				Boolean updateRoom = Util.resultCheck(roomMapper.updateRoom(dbMap));
				if(!updateRoom) {
					throw new ApiException("001008", commonConstant.M_001008, "방 수정에 실패!");
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
	    		
    		}else {
    			throw new ApiException(svcTokenInfo.getResultCode(), svcTokenInfo.getResultMsg());
    		}
		
		}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[fastStart] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[fastStart] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
    		return ResponseEntity.status(HttpStatus.OK)
					.body(FastStartDto
							.builder()
							.resultCode(e.getErrorCode())
                            .resultMsg(e.getMessage())
							.build());
		}catch (Exception e) {
			logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(FastStartDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		
		return ResponseEntity.created(new URI("/login"))
				.body(FastStartDto
						.builder()
						.roomCode(liveUser.getRoomCode())
						.roomToken(liveUser.getRoomToken())
//						.svcCode(liveUser.getSvcCode())
						.roomType((String) paramMap.get("roomType"))
						.endDt((String) paramMap.get("endDt"))
						.maxPeople(liveUser.getMaxPeople())
						.quality(liveUser.getQuality())
						.screenType(liveUser.getScreenType())
						.resultCode("000000")
						.resultMsg(commonConstant.M_000000)
						.build());

	}
	
	public MonitoringRoomLogDto getRoomLogRow(HashMap<String, Object> paramMap) {
		return roomMapper.getRoomLogRow(paramMap);
	}
	
	public String checkRoomManagerId(HashMap<String, Object> paramMap) {
		return roomMapper.checkRoomManagerId(paramMap);
	}

	public ResponseEntity<CommonResDto> reStartStatus(
			HttpServletRequest request
			, HashMap<String, Object> paramMap
			, String key) {
    	try {
    		
    		
    		logger.info("[reStartStatus] paramMap=" + paramMap.toString());
    		// step1. parameter 검증
    		if(StringUtil.isEmpty(paramMap.get("svcToken"))
    			||StringUtil.isEmpty(paramMap.get("roomCode"))
//    			|| StringUtil.isEmpty(paramMap.get("userId"))
    		){
    			throw new ApiException("000001", commonConstant.M_000001);
    		}
    		
    		SvcTokenInfoResponseDto svcTokenInfo = commonTokenService.getSvcTokenInfo(request, paramMap);
    		
    		if("000000".equals(svcTokenInfo.getResultCode())) {
    			// step2. 회의정보 검증
    			paramMap.put("svcCode", svcTokenInfo.getSvcCode());
    			RoomCheckDto checkRoom = getRoomRow(paramMap);
    			
    			if(checkRoom == null) {
    				throw new ApiException("001001", commonConstant.M_001001, "상태변경 권한 없음!");
    			}
    			
    			if(!checkRoom.getRoomStatus().equals(commonConstant.ROOM_STATUS_CLOSE) 
//    					|| !checkRoom.getRegId().equals((String) paramMap.get("userId"))) {
    					|| !checkRoom.getRegId().equals(svcTokenInfo.getUserId())) {
    				throw new ApiException("001012", commonConstant.M_001012, "상태변경 권한 없음!");
    			}
    			
    			paramMap.put("roomStatus", commonConstant.ROOM_STATUS_RUNNING);
    			int updateRoomStatus = roomMapper.updateRoomStatus(paramMap);
    			if (updateRoomStatus < 1) {
    				throw new ApiException("000002", commonConstant.M_000002, "상태변경 수정 불가!");
    			}
    		}else {
    			throw new ApiException(svcTokenInfo.getResultCode(), svcTokenInfo.getResultMsg());
    		}
    		
    	}catch (ApiException e) {
    		if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[reStartStatus] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[reStartStatus] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.OK)
	    			.body(CommonResDto
	    					.builder()
	    					.resultCode(e.getErrorCode())
	    					.resultMsg(e.getMessage())
	    					.build());
    	}catch (Exception e) {
    		logger.info("000003", commonConstant.M_000003, e.getMessage());
    		return ResponseEntity.status(HttpStatus.OK)
	    			.body(CommonResDto
	    					.builder()
	    					.resultCode("000003")
	    					.resultMsg(commonConstant.M_000003)
	    					.build());
		}
		
		// step3. 결과값
    	return ResponseEntity.status(HttpStatus.OK)
    			.body(CommonResDto
    					.builder()
    					.resultCode("000000")
    					.resultMsg(commonConstant.M_000000)
    					.build());  
	}

	private RoomCheckDto getRoomRow(HashMap<String, Object> paramMap) {
		return roomMapper.getRoomRow(paramMap);
	}
}