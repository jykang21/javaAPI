package kr.co.softbridge.sobroplatform.commons.util;

/**
 * <pre>
 * sobroplatform
 * Util.java
 * </pre>
 * 
 * @Author	: 
 * @Date 	: 
 * @Version	: 
 */
public class Util {
	
	/* int 결과값을 받아 boolean으로 리턴*/
	public static Boolean resultCheck(int Check) {
		
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
	
	/* 문자열이 숫자인지 확인 - 숫자가 아니면 false */
	public static Boolean checkNumberic(String str) {
		
		Boolean result = true;
		
		for(int i = 0; i < str.length(); i++) {
			if(!Character.isDigit(str.charAt(i))) {
				result = false;
			}
		}
		
		return result;
		
	}
	
	/* 바이트 길이 */
	public static int getByteLength(String str, String charset) {
		try {
			return str.getBytes(charset).length;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}