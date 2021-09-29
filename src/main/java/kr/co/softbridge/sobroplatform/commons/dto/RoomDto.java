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
public class RoomDto {
	@ApiParam(value = "방송코드", required = true, example = "1")
	private	int		roomCode;
	
	@ApiParam(value = "방송타입", required = false, example = "1")
	private	String	roomType;
	
	@ApiParam(value = "사용자ID", required = false, example = "admin01")
	private	String	memberId;
	
	@ApiParam(value = "사용자순번", required = false, example = "2")
	private	int		memberSeq;
	
	@ApiParam(value = "최대 참여자 수", required = false, example = "50")
	private	int		maxPeople;
	
	@ApiParam(value = "방송 제목", required = false, example = "20XX회 화상회의")
	private	String	title;
	
	@ApiParam(value = "방송품질", required = false, example = "0")
	private	String	quality;
	
	@ApiParam(value = "방송 시작일시", required = false, example = "20210101000000")
	private	String	startDt;
	
	@ApiParam(value = "방송 종료일시", required = false, example = "20210101235959")
	private	String	endDt;
	
	@ApiParam(value = "노출 여부", required = false, example = "Y|N")
	private	String	viewYn;
	
	@ApiParam(value = "방송 비밀번호", required = false, example = "AES256")
	private	String	roomPw;
	
	@ApiParam(value = "방송 상태", required = false, example = "01")
	private	String	roomStatus;
	
	@ApiParam(value = "녹화타입", required = false, example = "1|2")
	private	String	recType;
	
	@ApiParam(value = "삭제여부", required = false, example = "Y/N")
	private	String	delYn;
	@ApiParam(value = "서비스 코드", required = false, example = "21q0f9jf09jq02")
	private String svcCode;
	@ApiParam(value = "화면공유 유형", required = true, example = "01")
	private String screenType;
	@ApiParam(value = "예약시간도래전여부", required = false, example = "Y")
	private String startArrival;
}
