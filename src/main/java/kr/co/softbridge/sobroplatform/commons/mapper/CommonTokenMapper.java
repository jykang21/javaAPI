package kr.co.softbridge.sobroplatform.commons.mapper;

import java.util.HashMap;
import java.util.Map;

import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;
import kr.co.softbridge.sobroplatform.commons.dto.SobroAuthDto;
import kr.co.softbridge.sobroplatform.commons.dto.SvcAuthDto;
import kr.co.softbridge.sobroplatform.commons.dto.SvcInfoDto;

@PrimaryMapper
public interface CommonTokenMapper {

	SobroAuthDto getSobroAuth(HashMap<String, Object> dbMap);

	SvcAuthDto getSvcAuth(HashMap<String, Object> dbMap);

	SvcInfoDto getSvcInfo(Map<String, Object> dbMap);
	
	String getTokenIdx();
	
	int insertToken(Map<String, Object> paramMap);

	int insertSvcAuth(Map<String, Object> dbMap);

	int updateSvcAuth(HashMap<String, Object> dbMap);
}
