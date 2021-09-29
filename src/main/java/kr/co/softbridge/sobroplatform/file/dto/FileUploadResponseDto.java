package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 파일 업로드 응답 Dto
 */
@Getter
@Setter
@Builder
public class FileUploadResponseDto {
    private String roomCode;

    private String fileRealNm;

    private String fileNm;

    private String filePath;

    private String startDt;

    private String endDt;

    private String shareType;

    private String fileUrl;

    private String fileSize;
    
    private int fileSeq;
    
    private String resultCode;
    
    private String resultMsg;

}
