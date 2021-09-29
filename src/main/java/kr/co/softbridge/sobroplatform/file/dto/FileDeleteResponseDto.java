package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 파일 삭제 응답 Dto
 */
@Getter
@Setter
public class FileDeleteResponseDto {

    private String roomCode;

    private int fileSeq;

    private String fileReal_nm;

    private String fileNm;

    private String regId;

    private String udtId;

    private String udtDt;

    private String filePath;

    private String resultCode;
    
    private String resultMsg;
    
    private String fileType;
    
    private String vodType;
}
