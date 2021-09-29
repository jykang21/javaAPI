package kr.co.softbridge.sobroplatform.room.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
    private String roomCode;
//	@ApiParam(value = "사용자 ID", required = true, example = "admin01")
//    private String memberId;
	@ApiParam(value = "제목", required = true, example = "20XX회 화상회의")
    private String title;
	@ApiParam(value = "품질", required = true, example = "0")
    private String quality;
	@ApiParam(value = "방송 시작날짜", required = true, example = "20210101000000")
    private String startDt;
	@ApiParam(value = "방송 종료날짜", required = true, example = "20210102000000")
    private String endDt;
	@ApiParam(value = "노출 여부", required = true, example = "Y")
    private String viewYn;
	@ApiParam(value = "최대 참여자 수", required = true, example = "100")
    private String maxPeople;
	@ApiParam(value = "방 비밀번호", required = true, example = "1234")
    private String roomPw;
	@ApiParam(value = "방송 상태", required = true, example = "종료")
    private String roomStatus;
//	@ApiParam(value = "서비스코드", required = true, example = "종료")
//    private String svcCode;
	
	public String toStringShortPrefix() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
