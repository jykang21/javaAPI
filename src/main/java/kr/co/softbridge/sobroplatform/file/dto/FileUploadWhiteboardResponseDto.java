package kr.co.softbridge.sobroplatform.file.dto;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import kr.co.softbridge.sobroplatform.file.dto.FileUploadResponseDto.FileUploadResponseDtoBuilder;

/**
 * 화이트보드 파일 업로드 요청 Dto
 */
@Getter
@Setter
public class FileUploadWhiteboardResponseDto {

    private String imageUrl;
    private String roomCode;
    
    private String resultCode;
    private String resultMsg;

    public String toStringShortPrefix() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
