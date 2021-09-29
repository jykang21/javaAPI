package kr.co.softbridge.sobroplatform.sample.dto;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class SampleinputDto {
	@ApiParam(value = "사용자 ID", required = false, example = "abc1234")
    private String userId;
    @ApiParam(value = "방송번호", required = false, example = "1234")
    private int roomNo;
    @ApiParam(value = "인증Key", required = false, example = "soboro")
    private String encryptKey;
    @ApiParam(value = "암호화대상", required = false, example = "안녕하세요")
    private String target;
}
