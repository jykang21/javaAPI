package kr.co.softbridge.sobroplatform.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.co.softbridge.sobroplatform.commons.dto.TokenResDto;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtUtil {
	private static final Logger logger = LogManager.getLogger(JwtUtil.class);

    private static Key key;

    public JwtUtil(String secret){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String createToken(String id, String name) {


        String token = Jwts.builder()
                .claim("userId", id)
                .claim("name", name)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return token;
    }
    
    public static TokenResDto createToken(Object user, String tokenType, String type, Date endDt) {
    	String token = "";
    	HashMap<String, Object> claims = new HashMap<String, Object>();
    	TokenResDto tokenRes = new TokenResDto();

		try {
			//Header 부분 설정
	        Map<String, Object> headers = new HashMap<>();
	        headers.put("typ", "JWT");
	        headers.put("alg", "HS512");
	        
	        Long expiredTime = 1000 * 60L * 60L * 1L; // 토큰 유효 시간 (1시간)
	        
	        Date act = new Date(); // 토큰 생성 시간
	        Date ext = new Date(); // 토큰 만료 시간
	        if("R".equals(type)) {
	        	expiredTime = (endDt.getTime() - act.getTime()) + (1000 * 60L * 30L);
	        }
	        ext.setTime(ext.getTime() + expiredTime);
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, Object> temp = objectMapper.convertValue(user, Map.class);
	        for(String key : temp.keySet()) {
	        	claims.put(key, temp.get(key));
	        }
	     
	        // 토큰 Builder
//	        for(java.lang.reflect.Field field : user.getClass().getDeclaredFields()) {
//	        	field.setAccessible(true);
//	        	Object value;
//				value = field.get(user);
//	        	claims.put(field.getName(), value);
//	        }        
	        token = Jwts.builder()
	        		.setHeader(headers) // Headers 설정
	        		.setClaims(claims)
	        		.setSubject(tokenType) // 토큰 용도
	        		.setIssuedAt(act) // 토큰 생성 시간 설정
	                .setExpiration(ext) // 토큰 만료 시간 설정
	        		.signWith(key, SignatureAlgorithm.HS512)
	        		.compact();
	        
	        tokenRes.setToken(token);
	        tokenRes.setExpDate(ext);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return tokenRes;
    }
    
    public Map<String, Object> verifyJWT(String jwt) throws UnsupportedEncodingException {
        Map<String, Object> claimMap = null;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key) // Set Key
                    .parseClaimsJws(jwt) // 파싱 및 검증, 실패 시 에러
                    .getBody();

            claimMap = claims;

            //Date expiration = claims.get("exp", Date.class);
            //String data = claims.get("data", String.class);
            
        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
        	logger.error("verifyJWT ExpiredJwtException ", e.getMessage());
        } catch (Exception e) { // 그외 에러났을 경우
        	logger.error("verifyJWT Exception ", e.getMessage());
        }
        return claimMap;
    }    
}
