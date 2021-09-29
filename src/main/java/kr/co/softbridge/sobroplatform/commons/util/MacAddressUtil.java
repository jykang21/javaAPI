package kr.co.softbridge.sobroplatform.commons.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import kr.co.softbridge.sobroplatform.commons.service.TloLogService;

public class MacAddressUtil {

	/**
	 * 로컬 맥 주소를 가져오는 메소드
	 *
	 * Created by 닢향
	 * http://niphyang.tistory.com
	 */
	public static String getLocalMacAddress() {
	 	String result = "";
		InetAddress addr;

		try {
			addr = InetAddress.getLocalHost();
		   
			NetworkInterface network = NetworkInterface.getByInetAddress(addr);
			byte[] mac = network.getHardwareAddress();
		   
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
				result = sb.toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e){
			e.printStackTrace();
		}
		    
		return result;
	}

    public static HashMap<String, Object> isDevice(HttpServletRequest request) {
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	String agent = request.getHeader("User-Agent");
    	String forwardedIp = request.getHeader("X-FORWARDED-FOR");
    	String proxyClientIp = request.getHeader("Proxy-Client-IP"); 
    	String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
    	String httpClientIp = request.getHeader("HTTP_CLIENT_IP");
    	String remoteAddrIp = request.getRemoteAddr();
    	String brower = null;
    	String device = null;
    	boolean isMobile = false;
    	String os = "PC";

    	if (agent != null) {
    		if(agent.indexOf("Mobile") > -1) {
    			isMobile = true;
    		}
    		
    		if(isMobile) {
    			if (agent.indexOf("iPhone") > -1 && agent.indexOf("Mobile") > -1) { 
    				os = "IOS"; 
    	    	} else if (agent.indexOf("Android") > -1 && agent.indexOf("Mobile") > -1) { 
    	    		os = "AOS"; 
    	    	} 
    			
    			String[] mobileos = {"iPhone", "iPod", "Android", "BlackBerry", "Windows CE", "Nokia", "Webos", "Opera Mini", "SonyEricsson", "Opera Mobi", "IEMobile"};

    	    	if (agent != null && !agent.equals("")) {
    	    		for(int i = 0; i < mobileos.length; i++) {
    	    			if (agent.indexOf(mobileos[i]) > -1) {
    	    				device = mobileos[i];
    	    			}
    	    		}
    	    	}
    		} else {
    			if(agent.indexOf("NT 6.0") != -1) device = "Windows Vista/Server 2008"; 
    	    	else if(agent.indexOf("NT 5.2") != -1) device = "Windows Server 2003"; 
    	    	else if(agent.indexOf("NT 5.1") != -1) device = "Windows XP"; 
    	    	else if(agent.indexOf("NT 5.0") != -1) device = "Windows 2000"; 
    	    	else if(agent.indexOf("NT 7.0") != -1) device = "Windows 7";
    	    	else if(agent.indexOf("NT 8.0") != -1) device = "Windows 8";
    	    	else if(agent.indexOf("NT 10.0") != -1) device = "Windows 10";   
    	    	else if(agent.indexOf("NT") != -1) device = "Windows NT"; 
    	    	else if(agent.indexOf("9x 4.90") != -1) device = "Windows Me"; 
    	    	else if(agent.indexOf("98") != -1) device = "Windows 98"; 
    	    	else if(agent.indexOf("95") != -1) device = "Windows 95"; 
    	    	else if(agent.indexOf("Win16") != -1) device = "Windows 3.x"; 
    	    	else if(agent.indexOf("Windows") != -1) device = "Windows"; 
    	    	else if(agent.indexOf("Linux") != -1) device = "Linux"; 
    	    	else if(agent.indexOf("Macintosh") != -1) device = "Macintosh"; 
    	    	else device = "";
    		}
    		
			if (agent.indexOf("Trident") > -1) { 
				brower = "MSIE"; 
	    	} else if (agent.indexOf("SamsungBrowser") > -1) { 
	    		brower = "SamsungBrowser"; 
	    	} else if (agent.indexOf("Opera") > -1) { 
	    		brower = "Opera"; 
	    	} else if (agent.indexOf("Chrome") > -1) { 
	    		brower = "Chrome"; 
	    	} else if (agent.indexOf("Safari") > -1) { 
	    		brower = "Safari"; 
	    	}
    	}

    	result.put("agent", 			agent);
    	result.put("forwardedIp",		forwardedIp);
    	result.put("proxyClientIp",		proxyClientIp);
    	result.put("wlProxyClientIp",	wlProxyClientIp);
    	result.put("httpClientIp",		httpClientIp);
    	result.put("remoteAddrIp",		remoteAddrIp);
    	result.put("brower",			brower);
    	result.put("device",			device);
    	result.put("isMobile",			isMobile);
    	result.put("os",				os);
    	
    	return result;
    }
}
