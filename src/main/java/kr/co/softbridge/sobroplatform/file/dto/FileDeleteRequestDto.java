package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * 파일 삭제 요청 Dto
 */
@Getter
@Setter
public class FileDeleteRequestDto {

    @NotEmpty
    private String roomCode;

    private int fileSeq;

    @NotEmpty
    private String userId;

    private LocalDateTime udtDt;

    private String delYn = "Y";
    
    private String roomToken;
    
    private String svcCode;
    
    private String userNm;

    public String toStringShortPrefix() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
