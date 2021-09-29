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
public class CommonGroupCodeDto {
	@ApiParam(value = "그룹코드", required = true, example = "01")
    private String grpCode;
	@ApiParam(value = "그룹명", required = true, example = "방송형태")
    private String grpNm;
	@ApiParam(value = "사용여부", required = false, example = "Y|N")
    private String useYn;
	@ApiParam(value = "그룹설명", required = false, example = "방송형태코드설명")
    private String grpDesc;

}
