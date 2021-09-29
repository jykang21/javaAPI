package kr.co.softbridge.sobroplatform.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;

/**
 * <pre>
 * sobroplatform
 * StringUtil.java
 * </pre>
 * 
 * @Author	: sb.jykang
 * @Date 	: 2021. 6. 10.
 * @Version	: 
 */
public class StringUtil {
	
	private static final Logger logger = LogManager.getLogger(MonitoringLogService.class);

	/**
	 * <pre>
	 * @Method Name : addZero
	 * 1. 개요 : 문자열 자리수 만큼 '0'으로 채움
	 * 2. 처리내용 : 
	 * 3. 작성자	: 2021. 6. 10.
	 * 4. 작성일	: sb.jykang
	 * </pre>
	 * 
	 * @Parameter	
	 *  	str 	: String 문자열
	 *  	length	: 총자릿수
	 * @ReturnType	: String
	 */
	public static String addZero (String str, int length) {
		String temp = "";
		for (int i = str.length(); i < length; i++)
			temp += "0";
		temp += str;
		return temp;
	}

	/**
	 * <pre>
	 * @Method Name : isEmpty
	 * 1. 개요 : String 문자열 검증
	 * 2. 처리내용 : 
	 * 		- 문자열의 값이 존재하는경우 false를 반환한다.
	 * 3. 작성자	: 2021. 6. 10.
	 * 4. 작성일	: sb.jykang
	 * </pre>
	 * 
	 * @Parameter	: String
	 * @ReturnType	: boolean
	 */
	public static boolean isEmpty(String str) {
		if(str == null || str.length() == 0) {
			return true;
		}else {
			return false;
		}
	}
	
	/*object의 null 체크*/
	public static boolean isEmpty(Object obj) {

		if(obj == null) return true;
		if ((obj instanceof String) && (((String)obj).trim().length() == 0)) { return true; }
	        if (obj instanceof Map) { return ((Map<?, ?>) obj).isEmpty(); }
	        if (obj instanceof Map) { return ((Map<?, ?>)obj).isEmpty(); } 
	        if (obj instanceof List) { return ((List<?>)obj).isEmpty(); }
	        if (obj instanceof Object[]) { return (((Object[])obj).length == 0); }
		return false;
	}
	
	public static boolean isNotEmpty(Object obj) {
		if(!isEmpty(obj)) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * <pre>
	 * @Method Name : null2void
	 * 1. 개요 : Object Null 체크
	 * 2. 처리내용 : 
	 * 		- 파라메터의 형식에 따라 Null인경우 공백을 반환하고 
	 * 		  Null이 아닌경우 공백제거후 String으로 반환한다.
	 * 3. 작성자	: 2021. 6. 10.
	 * 4. 작성일	: sb.jykang
	 * </pre>
	 * 
	 * @Parameter	: Object
	 * @ReturnType	: String
	 */
	public static String null2void(Object param) {
		String str = new String();

		if (param == null) {
			return "";
		}

		if (param instanceof String) {
			str = (String) param;
		} else if (param instanceof String[]) {
			str = ((String[]) param)[0];
		} else if (param instanceof Date) {
			str = ((Date)param).toString();
		} else {
			str = String.valueOf(param);
		}

		if (str.equals("")) {
			return "";
		} else {
			return str.trim();
		}
	}
	
	/**
	 * <pre>
	 * @Method Name : null2void
	 * 1. 개요 : Object Null 인경우 대체 텍스트로 리턴
	 * 2. 처리내용 : 
	 * 3. 작성자	: 2021. 7. 21.
	 * 4. 작성일	: sb.jykang
	 * </pre>
	 * 
	 * @Parameter	: 
	 * @ReturnType	: 
	 */
	public static String null2void(Object param, String val) {
		String str = new String();

		if (param == null) {
			return val;
		}

		if (param instanceof String) {
			str = (String) param;
		} else if (param instanceof String[]) {
			str = ((String[]) param)[0];
		} else if (param instanceof Date) {
			str = ((Date)param).toString();
		} else {
			str = String.valueOf(param);
		}

		if (str.equals("")) {
			return val;
		} else {
			return str.trim();
		}
	}

	/**
	 * <pre>
	 * @Method Name : stringToTimeStamp
	 * 1. 개요 : TimeStamp형식으로 변환
	 * 2. 처리내용 : 
	 * 		- String문자열 형식의 값을 TimeStamp으로 변환하여 반환한다.
	 * 3. 작성자	: 2021. 6. 10.
	 * 4. 작성일	: sb.jykang
	 * </pre>
	 * 
	 * @Parameter	: String
	 * @ReturnType	: Timestamp
	 */
	public static java.sql.Timestamp stringToTimeStamp(String str, String pattern){

		if("".equals(null2void(str))){
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date parsedDate = new Date();

		try{
			parsedDate = sdf.parse(str);
		} catch(ParseException pe){
			pe.printStackTrace();
		}

		java.sql.Timestamp writeDate = new  java.sql.Timestamp(parsedDate.getTime());

		return writeDate;
	}


	/**
	 * <pre>
	 * @Method Name : stringToDateString
	 * 1. 개요 : 날짜 Format제거
	 * 2. 처리내용 : 
	 * 		- 포멧이 적용된 날짜형식의 문자열을 Format을 제거하여 반환한다.
	 * 3. 작성자	: 2021. 6. 10.
	 * 4. 작성일	: sb.jykang
	 * </pre>
	 * 
	 * @Parameter	: 
	 * @ReturnType	: 
	 */
	public static String stringToDateString(String str){

		if("".equals(null2void(str))){
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date parsedDate = new Date();
		SimpleDateFormat sts = new SimpleDateFormat ("yyyyMMddhhmmss");



		try{
			parsedDate = sdf.parse(str);
		} catch(ParseException pe){
			pe.printStackTrace();
		}

		java.sql.Timestamp writeDate = new  java.sql.Timestamp(parsedDate.getTime());

		return sts.format(writeDate);
	}
	
	public static String gapDateTime(String startDt, String endDt) {
		// Custom date format
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");  

		Date d1 = new Date();
		Date d2 = new Date();
		try {
		    d1 = format.parse(startDt);
		    d2 = format.parse(endDt);
		} catch (ParseException e) {
		    e.printStackTrace();
		}    
		
		
		// Get msec from each, and subtract.
		long diff = d2.getTime() - d1.getTime();
		long diffSeconds = ((diff%(60 * 60 * 1000))%(60 * 1000)) / 1000;         
		long diffMinutes = (diff%(60 * 60 * 1000)) / (60 * 1000);         
		long diffHours = diff / (60 * 60 * 1000);
		
		System.out.println("Time in seconds: " + diffSeconds + " seconds.");         
		System.out.println("Time in minutes: " + diffMinutes + " minutes.");         
		System.out.println("Time in hours: " + diffHours + " hours."); 


		return ((diffHours<10)?"0"+diffHours : diffHours) + ":" 
		+ ((diffMinutes<10)?"0"+diffMinutes : diffMinutes) + ":" 
		+ ((diffSeconds<10)?"0"+diffSeconds : diffSeconds);
	}
	
	public static JSONObject objectToJson(Object obj) {
		JSONObject json = new JSONObject();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, Object> temp = objectMapper.convertValue(obj, Map.class);
	        for(String key : temp.keySet()) {
	        	json.put(key, temp.get(key));
	        }
//	        for(java.lang.reflect.Field field : obj.getClass().getDeclaredFields()) {
//	        	field.setAccessible(true);
//	        	Object value;
//				value = field.get(obj);
//				json.put(field.getName(), value);
//	        }
		} catch (IllegalArgumentException e) {			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json;
    }
}