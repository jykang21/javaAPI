package kr.co.softbridge.sobroplatform.commons.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import kr.co.softbridge.sobroplatform.commons.constants.commonConstant;
import kr.co.softbridge.sobroplatform.commons.dto.CommonCodeDto;
import kr.co.softbridge.sobroplatform.commons.dto.CommonCodeResponeDao;
import kr.co.softbridge.sobroplatform.commons.mapper.CommonCodeMapper;
import kr.co.softbridge.sobroplatform.commons.util.StringUtil;

@Service
public class CommonCodeService {
	@Autowired
	private CommonCodeMapper commonCodeMapper;

	public ResponseEntity<CommonCodeResponeDao> codeList(String grpCode) {
    	Boolean result = false;
    	List<CommonCodeDto> codeList = null;
    	
    	try {
	    	if(StringUtil.isEmpty(grpCode)) {
	    		return ResponseEntity.status(HttpStatus.OK)
    					.body(CommonCodeResponeDao
    							.builder()
    							.result(result)
    							.error("000001")
                                .errorDescription(commonConstant.M_000001)
    							.build());
	    	}
	    	
	    	codeList = getCodeList(grpCode);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return ResponseEntity.status(HttpStatus.OK)
				.body(CommonCodeResponeDao
						.builder()
						.result(true)
						.codeList(codeList)
						.build());
	}
	
	public List<CommonCodeDto> getCodeList(String grpCode) {
		return commonCodeMapper.getCodeList(grpCode);
	}
}
