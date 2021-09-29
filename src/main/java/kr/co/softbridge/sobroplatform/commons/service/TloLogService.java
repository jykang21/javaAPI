package kr.co.softbridge.sobroplatform.commons.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import kr.co.softbridge.sobroplatform.commons.util.StringUtil;

@Service
public class TloLogService {

	private static final Logger logger = LogManager.getLogger(TloLogService.class);
	private static final Logger tloLogger = LogManager.getLogger("TLO");
	
	public static final String IS_MOBILE = "MOBILE";
//	private static final String IS_PHONE = "PHONE";
//	public static final String IS_TABLET = "TABLET";
	public static final String IS_PC = "PC";

	public void tloLog(HttpServletRequest request
			, String startDate
			, String endDate
			, String svcClass
			, String resultCode
			, HashMap<String, Object> param) {
		
		try {
			
			logger.info("[tloLog]startDate=[" + startDate + "], endDate=[" + endDate + "], reqResultCode=[" + resultCode + "], param=[" + param + "]");
			
			String pattern = "yyyyMMddHHmmss";
			String pattern2 = "yyyyMMddHHmmssSSS";
	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	    	SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
	    	String currTime = simpleDateFormat2.format(new Date());
	    	String logTime = simpleDateFormat.format(new Date());
	    	String seqId = currTime + currTime.substring(8, currTime.length() -1);
			
			/* 규격 설정*/
			String SEQ_ID			= seqId;													/* 로그 ID */
			String LOG_TIME			= logTime;													/* 로그 남기는 시간 */
			String LOG_TYPE			= "SVC";													/* SVC : 고정값 */
			String SID				= "";														/* SID : 빈값 */
			String RESULT_CODE		= StringUtil.null2void(resultCodeFind(resultCode), "");		/* 실행 결과 코드 */
			String REQ_TIME			= startDate;											/* 시작 시간 */
			String RSP_TIME			= endDate;											/* 종료 시간 */
			String CLIENT_IP		= StringUtil.null2void(getClientIP(request), "");			/* 접속 클라이언트 IP */
			String DEV_INFO			= StringUtil.null2void(isDevice(request), "");				/* 접속 단말 타입 : PC/MOBILE */
			String OS_INFO			= "";														/* OS 정보 : 빈값 */
			String NW_INFO			= "";														/* 접속 네트워크 정보 : 빈값*/
			String SVC_NAME			= "PLTFAPI";												/* 서비스 명 : 고정값 */
			String DEV_MODEL		= "";														/* 단말 모델명 : 빈값 */
			String CARRIER_TYPE		= "";														/* 통신구분 : 빈값 */
			String SVC_CODE			= StringUtil.null2void(param.get("svcCode"));				/* 서비스 요청 대상 코드 */
			String SVC_CLASS		= svcClass;													/* 서비스 코드 : 지정된 통합통계 코드 */
			String ROOM_CODE		= StringUtil.null2void(param.get("roomCode"));				/* 화상회의 방 ID */
			
			String tloMsg = "[TLO_LOG] SEQ_ID=" + SEQ_ID + "|LOG_TIME=" + LOG_TIME + "|LOG_TYPE=" + LOG_TYPE + "|SID=" + SID + "|RESULT_CODE=" + RESULT_CODE + "|REQ_TIME=" + REQ_TIME + 
					"|RSP_TIME=" + RSP_TIME + "|CLIENT_IP=" + CLIENT_IP + "|DEV_INFO=" + DEV_INFO + "|OS_INFO=" + OS_INFO + "|NW_INFO=" + NW_INFO + "|SVC_NAME=" + SVC_NAME + 
					"|DEV_MODEL=" + DEV_MODEL + "|CARRIER_TYPE=" + CARRIER_TYPE + "|SVC_CODE=" + SVC_CODE + "|SVC_CLASS=" + SVC_CLASS + "|ROOM_CODE=" + ROOM_CODE; 
			
			tloLogger.info(tloMsg);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/* 클라이언트 IP 가져오기 */
	public static String getClientIP(HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");
	    String header = "X-Forwarded-For";
	    
	    if (ip == null) {
	        ip = request.getHeader("Proxy-Client-IP");
	        header = "Proxy-Client-IP";
	    }
	    if (ip == null) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	        header = "WL-Proxy-Client-IP";
	    }
	    if (ip == null) {
	        ip = request.getHeader("HTTP_CLIENT_IP");
	        header = "HTTP_CLIENT_IP";
	    }
	    if (ip == null) {
	        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	        header = "HTTP_X_FORWARDED_FOR";
	    }
	    if (ip == null) {
	        ip = request.getRemoteAddr();
	        header = "remoteAddr";
	    }
	    logger.info("> Result : IP Address : " + ip + "Header : " + header);

	    return ip;
	}
	
	/* 모바일, 타블렛, PC 구분 */
	public static String isDevice(HttpServletRequest request) {
		if (request.getHeader("User-Agent") == null)	return IS_PC;
	    String userAgent = request.getHeader("User-Agent").toUpperCase();
		
	    if(userAgent.indexOf(IS_MOBILE) > -1) {
//	        if(userAgent.indexOf(IS_PHONE) == -1)
		    return IS_MOBILE;
//		else
//		    return IS_TABLET;
	    } else
	return IS_PC;
	}
	
	/* 통합 로그의 결과 코드와 플랫폼 결과 코드 매칭 */
	public String resultCodeFind(String resultCode) {
		
		String tloCode = "";
		
		if("000000".equals(resultCode)) {
			tloCode = "20000000";
		}else if("000001".equals(resultCode)) {
			tloCode = "40101010";
		}else if("003001".equals(resultCode)) {
			tloCode = "40101020";
		}else if("003002".equals(resultCode)) {
			tloCode = "40101030";
		}else if("003003".equals(resultCode)) {
			tloCode = "40101040";	
		}else if("000002".equals(resultCode)) {
			tloCode = "40101040";
		}else if("000003".equals(resultCode)) {
			tloCode = "40101050";
		}else if("000004".equals(resultCode)) {
			tloCode = "40101060";
		}else if("001001".equals(resultCode)) {
			tloCode = "40102010";
		}else if("001002".equals(resultCode)) {
			tloCode = "40102020";	
		}else if("001003".equals(resultCode)) {
			tloCode = "40102030";
		}else if("001004".equals(resultCode)) {
			tloCode = "40102040";
		}else if("001005".equals(resultCode)) {
			tloCode = "40103010";
		}else if("001006".equals(resultCode)) {
			tloCode = "40103020";
		}else if("001007".equals(resultCode)) {
			tloCode = "40103030";
		}else if("001008".equals(resultCode)) {
			tloCode = "40103040";
		}else if("001009".equals(resultCode)) {
			tloCode = "40103050";
		}else if("001010".equals(resultCode)) {
			tloCode = "40103060";
		}else if("001011".equals(resultCode)) {
			tloCode = "40103070";
		}else if("001012".equals(resultCode)) {
			tloCode = "40103080";
		}else if("002001".equals(resultCode)) {
			tloCode = "40104010";
		}else if("002002".equals(resultCode)) {
			tloCode = "40104020";
		}else if("002003".equals(resultCode)) {
			tloCode = "40104030";
		}else if("002004".equals(resultCode)) {
			tloCode = "40104040";
		}else if("002005".equals(resultCode)) {
			tloCode = "40104050";
		}else if("002006".equals(resultCode)) {
			tloCode = "40104060";
		}else if("004001".equals(resultCode)) {
			tloCode = "40105010";
		}else if("005001".equals(resultCode)) {
			tloCode = "40106010";
		}else if("005002".equals(resultCode)) {
			tloCode = "40106020";
		}else if("005003".equals(resultCode)) {
			tloCode = "40106030";
		}else if("005004".equals(resultCode)) {
			tloCode = "40106040";
		}else if("005005".equals(resultCode)) {
			tloCode = "40106050";
		}else if("005006".equals(resultCode)) {
			tloCode = "40106060";
		}else if("005007".equals(resultCode)) {
			tloCode = "40106070";
		}
		return tloCode;
		
	}

}
