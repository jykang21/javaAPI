package kr.co.softbridge.sobroplatform.commons.dto;

import java.util.Date;

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
public class SobroAuthDto {
	private String	tokenIdx;
	private String	siteCode;
	private String	token;
	private Date	expDate;
}
