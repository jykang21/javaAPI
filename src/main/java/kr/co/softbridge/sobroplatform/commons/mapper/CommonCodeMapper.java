package kr.co.softbridge.sobroplatform.commons.mapper;

import java.util.List;
import java.util.Map;

import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;
import kr.co.softbridge.sobroplatform.commons.dto.CommonCodeDto;
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringRealTimeRoomDto;

@PrimaryMapper
public interface CommonCodeMapper {

	List<CommonCodeDto> getCodeList(String grpCode);

	String getApiServiceNo();

	List<MonitoringRealTimeRoomDto> getRealTimeRoomList(Map<String, Object> paramMap);

	/* 토탈 카운트 */
	int getRealTimeRoomListTotalCount(Map<String, Object> paramMap);

}
