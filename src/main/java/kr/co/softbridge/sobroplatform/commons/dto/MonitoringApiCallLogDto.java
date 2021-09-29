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
public class MonitoringApiCallLogDto {
	@ApiParam(value = "API SERVICE 고유번호", required = true, example = "abcdxce")
	private	String	apiServiceNo;

	@ApiParam(value = "API SERVICE 이름", required = true, example = "api명")
	private	String	apiServiceNm;

	@ApiParam(value = "API 요청 코드", required = true, example = "C")
	private	String	apiReqServiceCode;

	@ApiParam(value = "요청시간", required = true, example = "")
	private	String	reqTime;

	@ApiParam(value = "요청결과코드", required = true, example = "")
	private	String	reqResultCode;

	@ApiParam(value = "서비스코드", required = true, example = "")
	private	String	svcCode;

	@ApiParam(value = "서비스명", required = true, example = "")
	private	String	svcNm;

	@ApiParam(value = "요청전문", required = true, example = "")
	private	String	reqText;

	@ApiParam(value = "응답시간", required = true, example = "")
	private	String	resTime;

	@ApiParam(value = "응답전문", required = true, example = "")
	private	String	resText;
}
