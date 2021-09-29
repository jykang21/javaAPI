package kr.co.softbridge.sobroplatform.sample.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.softbridge.sobroplatform.commons.dto.TokenReqDto;
import kr.co.softbridge.sobroplatform.commons.dto.TokenResDto;
import kr.co.softbridge.sobroplatform.commons.dto.UserDto;
import kr.co.softbridge.sobroplatform.commons.util.MacAddressUtil;
import kr.co.softbridge.sobroplatform.config.jwt.JwtUtil;
import kr.co.softbridge.sobroplatform.sample.dto.BroadListDto;
import kr.co.softbridge.sobroplatform.sample.dto.MockUserDto;
import kr.co.softbridge.sobroplatform.sample.dto.MultiDataSourcesRequestDto;
import kr.co.softbridge.sobroplatform.sample.dto.SampleOutputDto;
import kr.co.softbridge.sobroplatform.sample.service.SampleService;
import kr.co.softbridge.sobroplatform.security.AES256Util;
import kr.co.softbridge.sobroplatform.security.SHA256;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * <pre>
 * sobroplatform
 * SampleController.java
 * </pre>
 * 
 * @Author	: sb.jykang
 * @Date 	: 2021. 6. 4.
 * @Version	: 0.6
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sample")
public class SampleController {

    private static final Logger logger = LogManager.getLogger(SampleController.class);
    
    @Value("${platform.aes256.key}")
    private String key; 
    
    @Value("${jwt.secret}")
    private String secret; 

    private final SampleService sampleService;
    
    /**
     * <pre>
     * @Method Name : hello
     * 1. 개요 : 샘플로 초기페이지
     * 2. 처리내용 : 
     * 3. 작성자	: 2021. 6. 4.
     * 4. 작성일	: sb.jykang
     * </pre>
     */
    @ApiOperation(value = "접근 테스트", notes = "초기접근테스트")
    @GetMapping("/hello")
    public ResponseEntity<String> hello(HttpServletRequest request) {
    	MacAddressUtil.isDevice(request);
    	return ResponseEntity.ok("hello");
    }

    
    /**
     * <pre>
     * @Method Name : ase256Encrypt
     * 1. 개요 : ase256암호화 샘플 API
     * 2. 처리내용 : 
     * 		- 문자열에 대한 암호화 처리 후 반환한다.
     * 3. 작성자	: 2021. 6. 4.
     * 4. 작성일	: sb.jykang
     * </pre>
     * 
     * @Parameter	: input - inputHashMap
     * @ReturnType	: SampleOutputDto
     */
    @ApiOperation(value = "암호화 테스트", notes = "ASE256암호화")
    @PostMapping(value = "/ase256Encrypt", produces = MediaType.APPLICATION_JSON_VALUE)
	public HashMap<String, Object> ase256Encrypt(
            @RequestBody(required = true) String inputString 
			) throws Exception {
    	HashMap<String, Object> outData = new HashMap<String, Object>(); 
		logger.info("INFO SUCCESS");
		AES256Util ase256 = new AES256Util(key);
		SHA256 sha256 = new SHA256(); 
		outData.put("ase256", ase256.encrypt(inputString));
		outData.put("sha256", sha256.encrypt(inputString));
		
		return outData;
	}

    /**
     * <pre>
     * @Method Name : ase256Decrypt
     * 1. 개요 : ase256으로 암호화된 정보를 복호화
     * 2. 처리내용 : 
     * 		- 암호화된 문자열을 복호화하여 반환한다.
     * 3. 작성자	: 2021. 6. 4.
     * 4. 작성일	: sb.jykang
     * </pre>
     * 
     * @Parameter	: input - HashMap
     * @ReturnType	: HashMap<String, Object>
     */
    @ApiOperation(value = "복호화 테스트", notes = "ASE256복호화")
    @PostMapping(value = "/ase256Decrypt", produces = MediaType.APPLICATION_JSON_VALUE)
	public HashMap<String, Object> ase256Decrypt(
            @RequestBody(required = true) String encString 
			) throws Exception {
    	HashMap<String, Object> outData = new HashMap<String, Object>();
		logger.info("INFO SUCCESS");
		AES256Util ase256 = new AES256Util(key);
		outData.put("decrypt", ase256.decrypt(encString));
		
		return outData;
	}

    /**
     * <pre>
     * @Method Name : createJwtToken
     * 1. 개요 : 사용자계정으로 토큰을 생성한다.
     * 2. 처리내용 : 사용자정보로 사용자토큰을 생성한다.
     * 		- 내용
     * 3. 작성자	: 2021. 6. 8.
     * 4. 작성일	: sb.jykang
     * </pre>
     * 
     * @Parameter	: 
     * @ReturnType	: 
     */
    @ApiOperation(value = "JWT 토큰생성", notes = "사용자계정을 이용한 사용자토큰 생성")
    @PostMapping("/getCrateToken")
    public ResponseEntity<TokenResDto> createJwtToken(@RequestBody TokenReqDto resource) throws URISyntaxException {
        UserDto user = sampleService.authenticate(resource);
		/*
		 * UserDto user = new UserDto(); user.setUserId("test123");
		 * user.setUserName("김제형");
		 */
        JwtUtil jwtUtil = new JwtUtil(secret);
        // String accessToken = jwtUtil.createToken(user.getUserId(), user.getUserName());
        TokenResDto tokenRes = jwtUtil.createToken(user, "login-sample", "SP", null);
        String url ="/getCrateToken";
        return ResponseEntity.created(new URI(url)).body(TokenResDto
                .builder()
                .token(tokenRes.getToken())
                .build());
    }

    /**
     * 복수 데이터 소스(데이터베이스) 사용 예시
     * @return
     */
    @ApiOperation(value = "복수 데이터 소스 사용 예시", notes = "복수 데이터 소스 사용 예시")
    @GetMapping("/multi-data-sources")
    public ResponseEntity multiDataSourcesTest(MultiDataSourcesRequestDto requestDto) {
        List<BroadListDto> broadList = sampleService.findBroadListByBroadCode(requestDto.getBroadCode());
        List<MockUserDto> mockUsers = sampleService.fetchMockUsers();

        return ResponseEntity.ok(broadList);
    }
}
