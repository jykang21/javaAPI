package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 파일 조회 요청 Dto
 */
@Getter
@Setter
public class FileLookUpRequestDto {
    @NotNull
    private String roomCode;

    private String targetNm;

    private String regId;

    private String endDt;
}
