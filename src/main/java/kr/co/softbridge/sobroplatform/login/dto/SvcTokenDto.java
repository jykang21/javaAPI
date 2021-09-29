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
public class SvcTokenDto {
	@ApiParam(value = "서비스코드", required = true, example = "sobro")
    private String svcCode;
	@ApiParam(value = "서비스 도메인", required = true, example = "https://www.sobro.co.kr")
    private String svcDomain;
	@ApiParam(value = "서비스 명", required = true, example = "비대면 플랫폼")
    private String svcNm;
	private String pin;
    private String userId;
}
