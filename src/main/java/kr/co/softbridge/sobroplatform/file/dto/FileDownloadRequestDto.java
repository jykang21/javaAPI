package kr.co.softbridge.sobroplatform.file.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Getter;
import lombok.Setter;

/**
 * 파일 삭제 요청 Dto
 */
@Getter
@Setter
public class FileDownloadRequestDto {

	private String roomToken;
    private String svcCode;
    private String roomCode;
    private String userId;
    private String userNm;
    private String filePath;
    private String fileType;
    private String vodType;
    private int fileSeq;
    
    public String toStringShortPrefix() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
