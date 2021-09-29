package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

/**
 * 파일 업로드 요청 Dto
 */
@Getter
@Setter
public class FileUploadRequestDto {
    @NotNull
    private MultipartFile file;
    
    /** 서비스 코드 */
    private String svcCode;
    
    /** 서비스구분 */
    @NotNull
    private String svcTarget;
    
    /** 방송 코드 */
    private String roomCode;

    /** 파일 순번 */
    private int fileSeq;
    private int FILE_SEQ;

    /** 업로드 요청 파일명 */
    private String fileRealNm;

    /** 서버에 저장된 파일명 */
    private String fileNm;
    
    /** 서버에 저장된 파일타입 */
    @Builder.Default
    private String fileType="01";

    /** 파일 경로 */
    private String filePath;

    /** 파일 확장자 */
    private String fileExt;
    
    /** 동영상유형 */
    @Builder.Default
    private String vodType="vod";

    /** 파일 크기 (KB) */
    private String fileSize="0";

    /** 노출 시작날짜 */
    private LocalDateTime startDt;

    /** 노출 종료날짜 */
    private LocalDateTime endDt;
    
    /** 노출여부 */
    @Builder.Default
    private String viewYn = "Y";

    /** 대상 사용자 */
    @Builder.Default
    private String shareType = "01";

    /** 삭제 여부 */
    @Builder.Default
    private String delYn = "N";

    /** 파일 URL */
    private String fileUrl;
    
    /** 공유대상 그룸 */
    private String targetGroup;
    
    /** 공유대상 사용자 */
    private String targetId;
    
    /** 등록ID */
    private String regId;
    
    /** 등록일시 */
    private LocalDateTime regDt;
    
    /** 수정자ID */
    private String udtId;
    
    /** 수정일시 */
    private LocalDateTime udtDt;

    /** 사용자ID */
    @NotNull
    private String userId;
    
    @NotNull
    private String userNm;
    
    private String roomToken;
    
    public String toStringShortPrefix() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
