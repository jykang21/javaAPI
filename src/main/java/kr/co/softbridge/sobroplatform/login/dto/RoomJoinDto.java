package kr.co.softbridge.sobroplatform.login.dto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomJoinDto {
	@ApiParam(value = "사용자 ID", required = true, example = "abc1234")
    private String userId;
    @ApiParam(value = "방송번호", required = true, example = "1234")
    private String roomCode;
    @ApiParam(value = "사용자명", required = false, example = "홍길동")
    private String userNm;
    @ApiParam(value = "사용자레벨", required = false, example = "1001")
    private String userLevel;
	@ApiParam(value = "최대 참여자 수", required = false, example = "50")
	private	int		maxPeople;
    @ApiParam(value = "방송입장토큰인덱스", required = true, example = "1")
    private String tokenIdx;
    @ApiParam(value = "방송입장토큰", required = true, example = "AES256")
    private String roomToken;
    @ApiParam(value = "서비스토큰", required = true, example = "909sag3ds9ff")
    private String svcToken;
//    @ApiParam(value = "방개설자 ID", required = true, example = "abc1234")
//    private String managerId;
	@ApiParam(value = "방송품질", required = false, example = "0")
	private	String	quality;
	@ApiParam(value = "응답 코드", required = true, example = "000000")
    private String resultCode;
	@ApiParam(value = "응답 메시지", required = true, example = "성공")
    private String resultMsg;
	@ApiParam(value = "화면공유 유형", required = true, example = "01")
	private String screenType;
}
