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
public class RoomTokenDto {
	
	private String userNm;
	private String roomCode;
	private String userId;
	private String pin;
	private String svcCode;
}
