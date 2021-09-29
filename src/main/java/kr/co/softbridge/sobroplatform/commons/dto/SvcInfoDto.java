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
public class SvcInfoDto {
	private String	svcSeq;
	private String	svcCode;
	private String	svcDomain;
	private String	svcNm;
	private String	useYn;
	private String	tokenIdx;
}
