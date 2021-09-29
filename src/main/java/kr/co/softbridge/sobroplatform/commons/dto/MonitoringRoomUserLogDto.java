package kr.co.softbridge.sobroplatform.commons.dto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringRoomUserLogDto {
	@ApiParam(value = "서비스코드", required = true, example = "abcdxce")
	private	String	svcCode;
	
	@ApiParam(value = "사용자ID", required = false, example = "1")
	private	String	userId;
	
	@ApiParam(value = "사용자명", required = false, example = "50")
	private	String	userNm;
	
	@ApiParam(value = "MAC주소", required = false, example = "ab:cd:ef:gh")
	private	String	macAddress;
	
	@ApiParam(value = "디바이스ID", required = false, example = "20210101000000")
	private	String	deviceId;
	
	@ApiParam(value = "방 코드", required = false, example = "")
	private	String	roomCode;
}
