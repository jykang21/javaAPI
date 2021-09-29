package kr.co.softbridge.sobroplatform.commons.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonCodeResponeDao {
	private Boolean						result;
	private String 						error;
	private List<CommonGroupCodeDto>	grpList;
	private List<CommonCodeDto> 		codeList;
    private String						errorDescription;
}
