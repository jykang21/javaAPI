package kr.co.softbridge.sobroplatform.commons.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResDto implements Serializable{

    private String token;
    private Date expDate;
    private String tokenIdx;
    private String siteCode;
}
