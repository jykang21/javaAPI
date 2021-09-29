package kr.co.softbridge.sobroplatform.sample.dto;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class SampleOutputDto {
	@ApiParam(value = "사용자 ID", required = false, example = "abc1234")
    private String userId;
    @ApiParam(value = "방송번호", required = false, example = "1234")
    private int roomNo;
    @ApiParam(value = "양방향 암호화", required = false, example = "asdjflaowern==")
    private String ase256Encrypt;
    @ApiParam(value = "단방향 암호화", required = false, example = "asdjflaowern==")
    private String sha256Encrypt;
}
