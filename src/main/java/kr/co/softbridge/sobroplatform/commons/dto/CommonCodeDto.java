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
public class CommonCodeDto {
	@ApiParam(value = "그룹코드", required = true, example = "01")
    private String grpCode;
	@ApiParam(value = "공통코드", required = true, example = "01")
    private String codeId;
	@ApiParam(value = "공통코드명", required = true, example = "화상회의")
    private String codeNm;
	@ApiParam(value = "사용여부", required = false, example = "Y|N")
    private String useYn;
	@ApiParam(value = "코드설명", required = false, example = "방송형태코드설명")
    private String codeDesc;
}
