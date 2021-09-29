package kr.co.softbridge.sobroplatform.commons.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.RoomVerifyResponseDto;
import kr.co.softbridge.sobroplatform.commons.dto.SobroAuthDto;
import kr.co.softbridge.sobroplatform.commons.dto.SvcAuthDto;
import kr.co.softbridge.sobroplatform.commons.dto.SvcInfoDto;
import kr.co.softbridge.sobroplatform.commons.dto.SvcTokenInfoResponseDto;
import kr.co.softbridge.sobroplatform.commons.dto.TokenResDto;
import kr.co.softbridge.sobroplatform.commons.dto.TokenRoomDto;
import kr.co.softbridge.sobroplatform.commons.exception.ApiException;
import kr.co.softbridge.sobroplatform.commons.mapper.CommonTokenMapper;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;
import kr.co.softbridge.sobroplatform.commons.util.Util;
import kr.co.softbridge.sobroplatform.config.jwt.JwtUtil;
import kr.co.softbridge.sobroplatform.login.dto.SvcTokenDto;
import kr.co.softbridge.sobroplatform.login.dto.TokenDto;
import kr.co.softbridge.sobroplatform.room.mapper.RoomMapper;


@Service
public class CommonTokenService {
	
	private static final Logger logger = LogManager.getLogger(CommonTokenService.class);
	
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.type.USER}")
    private String USER; 
    
    @Autowired
    private CommonTokenMapper commonTokenMapper;
    
    @Autowired
    private RoomMapper roomMapper;
    
    /* 서비스 토큰 생성*/
    public TokenDto svcTokenMake(HttpServletRequest request, Map<String, Object> paramMap) {
		
    	TokenResDto tokenRes = new TokenResDto();
    	TokenDto token = new TokenDto();
    	
    	try {
    		logger.info("[svcTokenMake] paramMap=" + paramMap.toString());
    		HashMap<String, Object> dbMap = new HashMap<String, Object>();
			/*Paremter 검증*/
			if (
					StringUtil.isEmpty(paramMap.get("svcCode"))
			) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			dbMap.clear();
			dbMap.put("svcCode", paramMap.get("svcCode"));
			SvcInfoDto svcInfo = commonTokenMapper.getSvcInfo(dbMap);
			if(svcInfo == null) {
				throw new ApiException("002004", commonConstant.M_002004, "svcInfo is null");
			}
			
			
			/*토큰 생성*/
			SvcTokenDto svcToken = new SvcTokenDto();
			svcToken.setSvcCode(svcInfo.getSvcCode());
			svcToken.setSvcDomain(svcInfo.getSvcDomain());
			svcToken.setUserId(StringUtil.null2void(paramMap.get("userId")));
			
			JwtUtil jwtUtil = new JwtUtil(secret);
			tokenRes = jwtUtil.createToken(svcToken, USER, "S", null);
			
			dbMap.clear();
			dbMap.put("siteCode", paramMap.get("svcCode"));
			dbMap.put("token", tokenRes.getToken());
			dbMap.put("expDate", tokenRes.getExpDate());
			boolean check = Util.resultCheck(commonTokenMapper.insertToken(dbMap));
			
			if(!check) {
				throw new ApiException("002001", commonConstant.M_002001);
			}
			
			SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
			String tokenIdx = sobroAuth.getTokenIdx();
			logger.info("[tokenIdx] ==== " + tokenIdx);
			
			dbMap.clear();
			dbMap.put("svcTokenIdx", tokenIdx);
			dbMap.put("svcCode", svcInfo.getSvcCode());
			boolean svcCheck = Util.resultCheck(commonTokenMapper.insertSvcAuth(dbMap));
			if(!svcCheck) {
				throw new ApiException("000002", commonConstant.M_000002);
			}
			
			token.setSvcTokenIdx(tokenIdx);
			token.setTokenInfo(tokenRes.getToken());
			token.setResultCode("000000");
			token.setResultMsg(commonConstant.M_000000);
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[svcTokenMake] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[svcTokenMake] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			
			token.setResultCode(e.getErrorCode());
			token.setResultMsg(e.getMessage());
			
			return token;

		}catch (Exception e) {
			token.setResultCode("000003");
			token.setResultMsg(commonConstant.M_000003);
			e.printStackTrace();
		}
    	
    	return token;
    }
        
    /* 서비스 토큰 검증 결과:Boolean */
    public Boolean svcVerify(HttpServletRequest request, Map<String, Object> paramMap) {
		
		Boolean result = false;
		HashMap<String, Object> dbMap = new HashMap<String, Object>();
		
		try {
			logger.info("[svcVerify] paramMap=" + paramMap.toString());
			/* 필수 값 체크 */
			if(StringUtil.isEmpty(paramMap.get("svcToken"))) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/* 토큰이 유효한지 조회 (TB_SOBRO_AUTH) *//*SITE_CODE, TOKEN_IDX*/
			dbMap.clear();
			dbMap.put("token", paramMap.get("svcToken"));
			SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
			if(sobroAuth == null) {
				throw new ApiException("002004", commonConstant.M_002004, "sobroAuth is null");
			}
			
			Date now = new Date();
			Date expDt = new Date();
			try {
				Date currentTime = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				expDt = simpleDateFormat2.parse(simpleDateFormat.format(sobroAuth.getExpDate()));
				now = simpleDateFormat2.parse(simpleDateFormat.format(currentTime));
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			if(now.compareTo(expDt) >= 0) {
				/* 현재 시간이 종료일시보다 크다 토큰 만료시간 종료 */
				throw new ApiException("002006", commonConstant.M_002006);
			}
			
			
			/* 서비스 인증 정보 조회 (TB_SVC_AUTH) */ /*SVC_CODE*/
			dbMap.clear();
			dbMap.put("svcTokenIdx", sobroAuth.getTokenIdx());
			dbMap.put("svcCode", sobroAuth.getSiteCode());
			SvcAuthDto svcAuth = commonTokenMapper.getSvcAuth(dbMap);
			if(svcAuth == null) {
				throw new ApiException("002004", commonConstant.M_002004, "svcAuth is null");
			}
			
			/* 서비스 정보 조회 */
			dbMap.clear();
			dbMap.put("svcCode", svcAuth.getSvcCode());
			SvcInfoDto svcInfo = commonTokenMapper.getSvcInfo(dbMap);
			if(svcInfo == null) {
				throw new ApiException("002004", commonConstant.M_002004, "svcInfo is null");
			}
			
			/* 토큰 복호화 */
			Map<String, Object> tokenMap = new HashMap<String, Object>();
			String svcToken = StringUtil.null2void(paramMap.get("svcToken"));
			JwtUtil jwtUtil = new JwtUtil(secret);
			tokenMap = jwtUtil.verifyJWT(svcToken);
			
			/* 토큰 검증 */
			if(!tokenMap.get("svcCode").equals(svcInfo.getSvcCode())) {
				throw new ApiException("002004", commonConstant.M_002004, "svcCode Not Match");
			}
			if(!tokenMap.get("svcDomain").equals(svcInfo.getSvcDomain())) {
				throw new ApiException("002004", commonConstant.M_002004, "svcDomain Not Match");
			}
			
			/*POSTMAN 시도시 에러 발생*/
//			String referer = StringUtil.null2void(paramMap.get("svcDomain"), request.getHeader("referer"));
//			
//		    referer = referer.replace(request.getRequestURI(),"");
//		    
//		    String tempStr = referer.substring(referer.length()-1);
//		    if("/".equals(tempStr)) {
//		    	referer = referer.substring(0, referer.length()-1);
//		    }
//		    if(!tokenMap.get("svcDomain").equals(referer)) {
//		    	throw new ApiException("002004", commonConstant.M_002004, "referer Not Match");
//			}
			
			result = true;
			
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[svcVerify] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[svcVerify] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			
			return result;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
    
    /* 룸 토큰 검증 결과:Boolean */
    public Boolean roomVerify(HttpServletRequest request, Map<String, Object> paramMap) {
		
		Boolean result = false;
		HashMap<String, Object> dbMap = new HashMap<String, Object>();
		
		try {
			logger.info("[roomVerify] paramMap=" + paramMap.toString());
			/* 필수 값 체크 */
			if(StringUtil.isEmpty(paramMap.get("roomToken"))
			) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/* roomToken 검증 */
			/* 토큰이 유효한지 조회 (TB_SOBRO_AUTH) *//*SITE_CODE, TOKEN_IDX*/
			dbMap.clear();
			dbMap.put("token", paramMap.get("roomToken"));
			SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
			if(sobroAuth == null) {
				throw new ApiException("002004", commonConstant.M_002004, "sobroAuth is null(roomToken)");
			}

			Date now = new Date();
			Date expDt = new Date();
			try {
				Date currentTime = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				expDt = simpleDateFormat2.parse(simpleDateFormat.format(sobroAuth.getExpDate()));
				now = simpleDateFormat2.parse(simpleDateFormat.format(currentTime));
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			if(now.compareTo(expDt) >= 0) {
				/* 현재 시간이 종료일시보다 크다 토큰 만료시간 종료 */
				throw new ApiException("002006", commonConstant.M_002006);
			}
			
			/*token 복호화*/
			String token = (String) paramMap.get("roomToken");
			Map<String, Object> tokenMap = new HashMap<String, Object>();
			JwtUtil jwtUtil = new JwtUtil(secret);
			tokenMap = jwtUtil.verifyJWT(token);
			
			/*빈값 검증*/
			if (
					StringUtil.isEmpty(tokenMap.get("roomCode")) 
					|| StringUtil.isEmpty(tokenMap.get("userId")) 
					|| StringUtil.isEmpty(tokenMap.get("userNm"))
					|| StringUtil.isEmpty(tokenMap.get("svcCode"))
			) {
				return result;
			}
			
			/*roomCode 검증*/
			if(!tokenMap.get("roomCode").equals(paramMap.get("roomCode"))) {
				throw new ApiException("002004", commonConstant.M_002004, "roomCode Not Match");
			}
			
			/*userId 검증*/
			if(!tokenMap.get("userId").equals(paramMap.get("userId"))){
				throw new ApiException("002004", commonConstant.M_002004, "userId Not Match");
			}
			
			/*userNm 검증*/
			if(!tokenMap.get("userNm").equals(paramMap.get("userNm"))){
				throw new ApiException("002004", commonConstant.M_002004, "userNm Not Match");
			}
			
			/*svcCode 검증*/
			if(!tokenMap.get("svcCode").equals(sobroAuth.getSiteCode())){
				throw new ApiException("002004", commonConstant.M_002004, "svcCode Not Match");
			}
			
			result = true;
			
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[roomVerify] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[roomVerify] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			return result;
		}catch (Exception e) {
			logger.info(e.getMessage());
		}
		
		return result;
	}
	
    /* 서비스 토큰 검증 결과:SvcTokenInfoResponseDto */
	public SvcTokenInfoResponseDto getSvcTokenInfo(HttpServletRequest request, HashMap<String, Object> paramMap) {
		
		HashMap<String, Object> dbMap = new HashMap<String, Object>();
		SvcTokenInfoResponseDto svcTokenInfoResponseDto = new SvcTokenInfoResponseDto();
		
		try {
			logger.info("[getSvcTokenInfo] SVC TOKEN VERIFY START");
			/* 필수 값 체크 */
			if(StringUtil.isEmpty(paramMap.get("svcToken"))) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/* 토큰이 유효한지 조회 (TB_SOBRO_AUTH) *//*SITE_CODE, TOKEN_IDX*/
			dbMap.clear();
			dbMap.put("token", paramMap.get("svcToken"));
			SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
			if(sobroAuth == null) {
				throw new ApiException("002004", commonConstant.M_002004, "sobroAuth is null(svcToken)");
			}

			Date now = new Date();
			Date expDt = new Date();
			try {
				Date currentTime = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				expDt = simpleDateFormat2.parse(simpleDateFormat.format(sobroAuth.getExpDate()));
				now = simpleDateFormat2.parse(simpleDateFormat.format(currentTime));
				
			}catch (Exception e) {
				svcTokenInfoResponseDto.setResultCode("000003");
				svcTokenInfoResponseDto.setResultMsg(commonConstant.M_000003);
				e.printStackTrace();
				logger.info("[getSvcTokenInfo] SVC TOKEN VERIFY END");
			}
			
			if(now.compareTo(expDt) >= 0) {
				/* 현재 시간이 종료일시보다 크다 토큰 만료시간 종료 */
				throw new ApiException("002006", commonConstant.M_002006);
			}
			
			/* 서비스 인증 정보 조회 (TB_SVC_AUTH) */ /*SVC_CODE*/
			dbMap.clear();
			dbMap.put("svcTokenIdx", sobroAuth.getTokenIdx());
			dbMap.put("svcCode", sobroAuth.getSiteCode());
			SvcAuthDto svcAuth = commonTokenMapper.getSvcAuth(dbMap);
			if(svcAuth == null) {
				throw new ApiException("002004", commonConstant.M_002004, "svcAuth is null(svcToken)");
			}
			
			/* 서비스 정보 조회 */
			dbMap.clear();
			dbMap.put("svcCode", svcAuth.getSvcCode());
			SvcInfoDto svcInfo = commonTokenMapper.getSvcInfo(dbMap);
			if(svcInfo == null) {
				throw new ApiException("002004", commonConstant.M_002004, "svcInfo is null(svcToken)");
			}
			
			/* 토큰 복호화 */
			Map<String, Object> tokenMap = new HashMap<String, Object>();
			String svcToken = StringUtil.null2void(paramMap.get("svcToken"));
			JwtUtil jwtUtil = new JwtUtil(secret);
			tokenMap = jwtUtil.verifyJWT(svcToken);
			
			/* 토큰 검증 */
			if(!tokenMap.get("svcCode").equals(svcInfo.getSvcCode())) {
				throw new ApiException("002004", commonConstant.M_002004, "svcCode Not Match");
			}
			if(!tokenMap.get("svcDomain").equals(svcInfo.getSvcDomain())) {
				throw new ApiException("002004", commonConstant.M_002004, "svcDomain Not Match");
			}
			if(!tokenMap.get("userId").equals(paramMap.get("userId"))) {
				throw new ApiException("002004", commonConstant.M_002004, "userId Not Match");
			}
			
			/*POSTMAN 시도시 에러 발생*/
//			String referer = StringUtil.null2void(paramMap.get("svcDomain"), request.getHeader("referer"));
//		    referer = referer.replace(request.getRequestURI(),"");
//		    
//		    String tempStr = referer.substring(referer.length()-1);
//		    if("/".equals(tempStr)) {
//		    	referer = referer.substring(0, referer.length()-1);
//		    }
//		    if(!tokenMap.get("svcDomain").equals(referer)) {
//		    	throw new ApiException("002004", commonConstant.M_002004, "referer Not Match");
//			}
			
			/* 결과 세팅 */
			svcTokenInfoResponseDto.setSvcCode(svcInfo.getSvcCode());
			svcTokenInfoResponseDto.setSvcToken(svcToken);
			svcTokenInfoResponseDto.setScvTokenIdx(svcAuth.getSvcTokenIdx());
			svcTokenInfoResponseDto.setUserId(StringUtil.null2void(tokenMap.get("userId"), ""));
			svcTokenInfoResponseDto.setResultCode("000000");
			svcTokenInfoResponseDto.setResultMsg(commonConstant.M_000000);
			
			logger.info("[getSvcTokenInfo] SVC TOKEN VERIFY END");
		
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[getSvcTokenInfo] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[getSvcTokenInfo] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			svcTokenInfoResponseDto.setResultCode(e.getErrorCode());
			svcTokenInfoResponseDto.setResultMsg(e.getMessage());
			logger.info("[getSvcTokenInfo] SVC TOKEN VERIFY END");
		}catch (Exception e) {
			svcTokenInfoResponseDto.setResultCode("000003");
			svcTokenInfoResponseDto.setResultMsg(commonConstant.M_000003);
			e.printStackTrace();
			logger.info("[getSvcTokenInfo] SVC TOKEN VERIFY END");
		}
		return svcTokenInfoResponseDto;
	}
	
	/* 룸 토큰 검증 결과:Boolean */
    public RoomVerifyResponseDto getRoomTokenInfo(HttpServletRequest request, Map<String, Object> paramMap) {
		
    	RoomVerifyResponseDto roomVerifyResponseDto = new RoomVerifyResponseDto();
		HashMap<String, Object> dbMap = new HashMap<String, Object>();
		
		try {
			logger.info("[getRoomTokenInfo] ROOM TOKEN VERIFY START");
			/* 필수 값 체크 */
			if(StringUtil.isEmpty(paramMap.get("roomToken"))
			) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/* roomToken 검증 */
			/* 토큰이 유효한지 조회 (TB_SOBRO_AUTH) *//*SITE_CODE, TOKEN_IDX*/
			dbMap.clear();
			dbMap.put("token", paramMap.get("roomToken"));
			SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
			if(sobroAuth == null) {
				throw new ApiException("002004", commonConstant.M_002004, "sobroAuth is null(roomToken)");
			}
			
			Date now = new Date();
			Date expDt = new Date();
			try {
				Date currentTime = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				expDt = simpleDateFormat2.parse(simpleDateFormat.format(sobroAuth.getExpDate()));
				now = simpleDateFormat2.parse(simpleDateFormat.format(currentTime));
				
			}catch (Exception e) {
				roomVerifyResponseDto.setResultCode("000003");
				roomVerifyResponseDto.setResultMsg(commonConstant.M_000003);
				e.printStackTrace();
			}
			
			if(now.compareTo(expDt) >= 0) {
				/* 현재 시간이 종료일시보다 크다 토큰 만료시간 종료 */
				throw new ApiException("002006", commonConstant.M_002006);
			}
			
			/* room 유무 */
			dbMap.clear();
			dbMap.put("roomCode", paramMap.get("roomCode"));
			TokenRoomDto roomInfo = roomMapper.checkRoomCnt(dbMap);
			if(roomInfo == null) {
				throw new ApiException("001001", commonConstant.M_001001);
			}
			
			/*token 복호화*/
			String token = (String) paramMap.get("roomToken");
			Map<String, Object> tokenMap = new HashMap<String, Object>();
			JwtUtil jwtUtil = new JwtUtil(secret);
			tokenMap = jwtUtil.verifyJWT(token);
			
			/*빈값 검증*/
			if (
					StringUtil.isEmpty(tokenMap.get("roomCode")) 
					|| StringUtil.isEmpty(tokenMap.get("userId")) 
					|| StringUtil.isEmpty(tokenMap.get("userNm"))
					|| StringUtil.isEmpty(tokenMap.get("svcCode"))
			) {
				throw new ApiException("002003", commonConstant.M_002003);
			}
			
			/*roomCode 검증*/
			if(!tokenMap.get("roomCode").equals(paramMap.get("roomCode"))) {
				throw new ApiException("002004", commonConstant.M_002004, "roomCode Not Match");
			}
			
			/*userId 검증*/
			if(!tokenMap.get("userId").equals(paramMap.get("userId"))){
				throw new ApiException("002004", commonConstant.M_002004, "userId Not Match");
			}
			
			/*userNm 검증*/
			if(!tokenMap.get("userNm").equals(paramMap.get("userNm"))){
				throw new ApiException("002004", commonConstant.M_002004, "userNm Not Match");
			}
			
			/*svcCode 검증*/
			if(!tokenMap.get("svcCode").equals(roomInfo.getSvcCode())){
				throw new ApiException("002004", commonConstant.M_002004, "svcCode Not Match");
			}
			
			roomVerifyResponseDto.setSvcCode(roomInfo.getSvcCode());
			roomVerifyResponseDto.setResultCode("000000");
			roomVerifyResponseDto.setResultMsg(commonConstant.M_000000);
			
			logger.info("[getRoomTokenInfo] ROOM TOKEN VERIFY END");
			
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[getRoomTokenInfo] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[getRoomTokenInfo] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			roomVerifyResponseDto.setResultCode(e.getErrorCode());
			roomVerifyResponseDto.setResultMsg(e.getMessage());
			logger.info("[getRoomTokenInfo] ROOM TOKEN VERIFY END");
		}catch (Exception e) {
			roomVerifyResponseDto.setResultCode("000003");
			roomVerifyResponseDto.setResultMsg(commonConstant.M_000003);
			logger.info(e.getMessage());
			logger.info("[getRoomTokenInfo] ROOM TOKEN VERIFY END");
		}
		
		return roomVerifyResponseDto;
	}
    
    /* 룸 토큰 검증 결과:Boolean */
    public RoomVerifyResponseDto getRoomTokenCheck(Map<String, Object> paramMap) {
		
    	RoomVerifyResponseDto roomVerifyResponseDto = new RoomVerifyResponseDto();
		HashMap<String, Object> dbMap = new HashMap<String, Object>();
		
		try {
			logger.info("[getRoomTokenCheck] ROOM TOKEN VERIFY START");
			/* 필수 값 체크 */
			if(StringUtil.isEmpty(paramMap.get("roomToken"))
			) {
				throw new ApiException("000001", commonConstant.M_000001);
			}
			
			/* roomToken 검증 */
			/* 토큰이 유효한지 조회 (TB_SOBRO_AUTH) *//*SITE_CODE, TOKEN_IDX*/
			dbMap.clear();
			dbMap.put("token", paramMap.get("roomToken"));
			SobroAuthDto sobroAuth = commonTokenMapper.getSobroAuth(dbMap);
			if(sobroAuth == null) {
				throw new ApiException("002004", commonConstant.M_002004, "sobroAuth is null(roomToken)");
			}
			
			Date now = new Date();
			Date expDt = new Date();
			try {
				Date currentTime = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				expDt = simpleDateFormat2.parse(simpleDateFormat.format(sobroAuth.getExpDate()));
				now = simpleDateFormat2.parse(simpleDateFormat.format(currentTime));
				
			}catch (Exception e) {
				roomVerifyResponseDto.setResultCode("000003");
				roomVerifyResponseDto.setResultMsg(commonConstant.M_000003);
				e.printStackTrace();
			}
			
			if(now.compareTo(expDt) >= 0) {
				/* 현재 시간이 종료일시보다 크다 토큰 만료시간 종료 */
				throw new ApiException("002006", commonConstant.M_002006);
			}
			
			/* room 유무 */
			dbMap.clear();
			dbMap.put("roomCode", paramMap.get("roomCode"));
			TokenRoomDto roomInfo = roomMapper.checkRoomCnt(dbMap);
			if(roomInfo == null) {
				throw new ApiException("001001", commonConstant.M_001001);
			}
			
			/*token 복호화*/
			String token = (String) paramMap.get("roomToken");
			Map<String, Object> tokenMap = new HashMap<String, Object>();
			JwtUtil jwtUtil = new JwtUtil(secret);
			tokenMap = jwtUtil.verifyJWT(token);
			
			/*빈값 검증*/
			if (
					StringUtil.isEmpty(tokenMap.get("roomCode")) 
					|| StringUtil.isEmpty(tokenMap.get("userId")) 
					|| StringUtil.isEmpty(tokenMap.get("userNm"))
					|| StringUtil.isEmpty(tokenMap.get("svcCode"))
			) {
				throw new ApiException("002003", commonConstant.M_002003);
			}
			
			roomVerifyResponseDto.setSvcCode(roomInfo.getSvcCode());
			roomVerifyResponseDto.setResultCode("000000");
			roomVerifyResponseDto.setResultMsg(commonConstant.M_000000);
			
			logger.info("[getRoomTokenInfo] ROOM TOKEN VERIFY END");
			
		}catch (ApiException e) {
			if(StringUtil.isNotEmpty(e.getErrorLogMsg())) {
				logger.info("[getRoomTokenCheck] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getErrorLogMsg());
			}else {
				logger.info("[getRoomTokenCheck] ERROR_CODE=" + e.getErrorCode() + ", ERROR_MSG=" + e.getMessage());
			}
			roomVerifyResponseDto.setResultCode(e.getErrorCode());
			roomVerifyResponseDto.setResultMsg(e.getMessage());
			logger.info("[getRoomTokenCheck] ROOM TOKEN VERIFY END");
		}catch (Exception e) {
			roomVerifyResponseDto.setResultCode("000003");
			roomVerifyResponseDto.setResultMsg(commonConstant.M_000003);
			logger.info(e.getMessage());
			logger.info("[getRoomTokenCheck] ROOM TOKEN VERIFY END");
		}
		
		return roomVerifyResponseDto;
	}

}