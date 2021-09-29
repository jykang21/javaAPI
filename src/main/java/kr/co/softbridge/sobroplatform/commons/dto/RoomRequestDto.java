package kr.co.softbridge.sobroplatform.commons.dto;

import java.sql.Date;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequestDto {	
	@ApiParam(value = "조회권한", required = false, example = "A|M|U")
	private	String	searchAuth;
	
	@ApiParam(value = "방송코드", required = true, example = "1")
	private	int		roomCode;
	
	@ApiParam(value = "방송명", required = false, example = "주간회의")
	private	String	roomName;
	
	@ApiParam(value = "방송타입", required = false, example = "1")
	private	String	roomType;
	
	@ApiParam(value = "사용자ID", required = false, example = "admin01")
	private	String	memberId;
	
	@ApiParam(value = "조회 제목", required = false, example = "20XX회 화상회의")
	private	String	title;
	
	@ApiParam(value = "조회 시작일", required = false, example = "20210101")
	private	Date	searchStartDt;
	
	@ApiParam(value = "조회 종료일", required = false, example = "20210131")
	private	Date	searchEndDt;

}
