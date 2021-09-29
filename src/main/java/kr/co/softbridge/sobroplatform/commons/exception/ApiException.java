package kr.co.softbridge.sobroplatform.commons.exception;

public class ApiException extends Exception{
	
	private String errorCode = "";
	private String errorLogMsg = "";
	
	public ApiException(String code, String msg, String msg2) {
		super(msg);
		errorCode = code;
		errorLogMsg = msg2;
	}
	
	public ApiException(String code, String msg) {
		super(msg);
		errorCode = code;
	}
	
	public ApiException(String msg) {
		super(msg);
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public String getErrorLogMsg() {
		return errorLogMsg;
	}

}
