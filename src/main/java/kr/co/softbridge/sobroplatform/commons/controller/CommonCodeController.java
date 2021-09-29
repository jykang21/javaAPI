package kr.co.softbridge.sobroplatform.commons.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.softbridge.sobroplatform.commons.dto.CommonCodeResponeDao;
import kr.co.softbridge.sobroplatform.commons.service.CommonCodeService;
import kr.co.softbridge.sobroplatform.commons.service.MonitoringLogService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/common")
@RestController
public class CommonCodeController {
	private final CommonCodeService commonCodeService;

    @Autowired
    private MonitoringLogService monitoringLogService;
	
	@ApiOperation(value = "공통코드목록", notes = "room 목록을 조회")
    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonCodeResponeDao> codeList(@RequestBody(required = true) HashMap<String, Object> paramMap) throws Exception {
		String grpCode = (String) paramMap.get("grpCode");
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	String startServerTime = simpleDateFormat.format(new Date());
    	// 서비스 호출
    	ResponseEntity<CommonCodeResponeDao> result = commonCodeService.codeList(grpCode);

    	String endServerTime = simpleDateFormat.format(new Date());
    	
    	monitoringLogService.apiCallLog(startServerTime, endServerTime, "공통코드 조회", "R", result.getBody().getResult()?"0000":result.getBody().getError(), paramMap, result);
    	return result;
	}
}
