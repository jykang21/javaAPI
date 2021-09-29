package kr.co.softbridge.sobroplatform.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SvcInfoResponseDto {
	private String svcCode;
	private String svcToken;
	private String resultCode;
	private String resultMsg;
}
