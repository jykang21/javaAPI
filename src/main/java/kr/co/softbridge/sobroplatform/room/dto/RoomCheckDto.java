package kr.co.softbridge.sobroplatform.room.dto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomCheckDto {
	@ApiParam(value = "서비스코드", required = true, example = "abcdxce")
	private	String	svcCode;
	
	@ApiParam(value = "회의상태", required = true, example = "01")
	private	String	roomStatus;
	
	@ApiParam(value = "방송코드", required = true, example = "1")
	private	String	roomCode;
	
	@ApiParam(value = "방송 종료일시 도래여부", required = false, example = "y")
	private	String	checkEndDt;
	
	@ApiParam(value = "등록자ID", required = false, example = "streamer1")
	private	String	regId;
	
}
