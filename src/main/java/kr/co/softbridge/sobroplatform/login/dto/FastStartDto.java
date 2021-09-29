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
public class FastStartDto {
	@ApiParam(value = "방송번호", required = true, example = "1234")
    private String roomCode;
    @ApiParam(value = "방송입장토큰", required = true, example = "AES256")
    private String roomToken;
    @ApiParam(value = "서비스코드", required = true, example = "SOBRO")
    private String svcCode;
    @ApiParam(value = "방송타입", required = true, example = "01")
    private String roomType;
    @ApiParam(value = "종료일자", required = true, example = "2021-07-14 01:00:00")
    private String endDt;
	@ApiParam(value = "최대 참여자 수", required = false, example = "50")
	private	int		maxPeople;
	@ApiParam(value = "응답 코드", required = true, example = "000000")
    private String resultCode;
	@ApiParam(value = "응답 메시지", required = true, example = "성공")
    private String resultMsg;
	@ApiParam(value = "방송품질", required = false, example = "0")
	private	String	quality;
	@ApiParam(value = "화면공유 유형", required = true, example = "01")
	private String screenType;
}
