package kr.co.softbridge.sobroplatform.file.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileListResponseDto {
	private List<FileListDto>	fileList;
	private String resultCode;
	private String resultMsg;
	private int fileCnt;
}
