package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 파일 조회 응답 Dto
 */
@Getter
@Setter
@Builder
public class FileLookUpResponseDto {
    private String roomCode;

    private int fileSeq;

    private String fileRealNm;

    private String fileNm;

    private String filePath;

    private String fileExt;

    private String fileSize;

    private String startDt;

    private String endDt;

    private String targetNm;

    private String regId;

    private String regDt;

    private String udtId;

    private String udtDt;

    private String delYn;

    private String fileUrl;

}
