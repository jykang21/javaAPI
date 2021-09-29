package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 화이트보드 파일 업로드 요청 Dto
 */
@Getter
@Setter
public class FileUploadWhiteboardRequestDto {

	private String encodedFile;
    private String roomCode;
    private String fileName;
    private String fileExtension;
    
    private String roomToken;
    private String svcCode;
    private String userNm;
    private String userId;

    public String toStringShortPrefix() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
