package kr.co.softbridge.sobroplatform.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenReqDto {
	private String targetUrl;
	private String email;
    private String password;
}
