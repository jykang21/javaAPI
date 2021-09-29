package kr.co.softbridge.sobroplatform.commons.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomVerifyResponseDto{

    private String svcCode;
    private String resultCode;
    private String resultMsg;
    
    public String toStringShortPrefix() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
