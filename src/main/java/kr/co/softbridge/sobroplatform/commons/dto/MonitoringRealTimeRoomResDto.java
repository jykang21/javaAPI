package kr.co.softbridge.sobroplatform.commons.dto;

import java.util.List;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringRealTimeRoomResDto {
	@ApiParam(value = "방송 개수", required = true, example = "1")
    private int roomCnt;
	@ApiParam(value = "방송리스트", required = true, example = "1")
    private List<MonitoringRealTimeRoomDto> roomList;
	@ApiParam(value = "응답 코드", required = true, example = "000000")
    private String resultCode;
	@ApiParam(value = "응답 메시지", required = true, example = "성공")
    private String resultMsg;
}
