package kr.co.softbridge.sobroplatform.commons.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SvcTokenInfoResponseDto{

    private String svcToken;
    private String svcCode;
    private String resultCode;
    private String resultMsg;
    private String scvTokenIdx;
    private String userId;
}
