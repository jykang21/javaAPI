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
public class MonitoringRoomLogDto {
	@ApiParam(value = "서비스코드", required = true, example = "abcdxce")
	private	String	svcCode;
	
	@ApiParam(value = "서비스명", required = true, example = "다자간영상회의")
	private	String	svcNm;
	
	@ApiParam(value = "방송코드", required = true, example = "1")
	private	String	roomCode;
	
	@ApiParam(value = "방송타입", required = false, example = "1")
	private	String	roomType;
	
	@ApiParam(value = "최대 참여자 수", required = false, example = "50")
	private	String	roomPeople;
	
	@ApiParam(value = "방송 제목", required = false, example = "20XX회 화상회의")
	private	String	title;
	
	@ApiParam(value = "방송 시작일시", required = false, example = "20210101000000")
	private	String	startDt;
	
	@ApiParam(value = "방송 종료일시", required = false, example = "20210101235959")
	private	String	endDt;
	
	@ApiParam(value = "방송진행시간", required = false, example = "2:00:00")
	private	String	progDt;
	
	@ApiParam(value = "생성타입", required = false, example = "C")
	private	String	type;
	
	@ApiParam(value = "생성자ID", required = false, example = "streamer1")
	private	String	hostId;
	
}
