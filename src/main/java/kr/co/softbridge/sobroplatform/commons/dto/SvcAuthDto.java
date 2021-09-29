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
public class SvcAuthDto {
	private String	svcSeq;
	private String	svcCode;
	private String	svcTokenIdx;
	private String	roomTokenIdx;
	private String	useYn;
}
