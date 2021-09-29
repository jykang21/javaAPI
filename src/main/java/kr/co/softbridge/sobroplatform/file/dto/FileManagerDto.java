package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/***
 * 파일 관리 Dto
 */
@Getter
@Setter
@Builder
public class FileManagerDto {

    private String roomCode;

    private int fileSeq;

    private String fileRealNm;

    private String fileNm;

    private String filePath;

    private String fileExt;

    private String fileSize;

    private LocalDateTime startDt;

    private LocalDateTime endDt;

    private String targetNm;

    private String regId;

    private LocalDateTime regDt;

    private String udtId;

    private LocalDateTime udtDt;

    private String delYn;
}
