package kr.co.softbridge.sobroplatform.file.dto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileListDto {
	@ApiParam(value = "서비스코드", required = false, example = "")
    private String	svcCode;
	@ApiParam(value = "방송코드", required = true, example = "20100023")
    private String	roomCode;
	@ApiParam(value = "서비스구분코드", required = true, example = "R0")
    private String	svcTarget;
	@ApiParam(value = "서비스구분명", required = true, example = "방송")
    private String	svcTargetNm;
	@ApiParam(value = "파일번호", required = true, example = "123")
    private String	fileSeq;
	@ApiParam(value = "대상ID", required = true, example = "")
    private String	targetId;
	@ApiParam(value = "파일URL", required = false, example = "")
    private String	fileUrl;
	@ApiParam(value = "파일명", required = false, example = "")
    private String	fileNm;
	@ApiParam(value = "파일경로", required = false, example = "")
    private String	filePath;
	@ApiParam(value = "파일확장자", required = false, example = "")
    private String	fileExt;
	@ApiParam(value = "파일사이즈", required = false, example = "")
    private String	fileSize;
	@ApiParam(value = "파일타입", required = false, example = "")
    private String	fileType;
	@ApiParam(value = "노출시작일", required = false, example = "")
    private String	startDt;
	@ApiParam(value = "노출종료일", required = false, example = "")
    private String	endDt;
	@ApiParam(value = "공유타입", required = false, example = "")
    private String	shareType;
	@ApiParam(value = "등록자ID", required = false, example = "")
    private String	regId;
	@ApiParam(value = "등록자닉네임", required = false, example = "이태우")
    private String	regNm;
	@ApiParam(value = "등록일시", required = false, example = "")
    private String	regDt;
	@ApiParam(value = "수정자ID", required = false, example = "")
    private String	udtId;
	@ApiParam(value = "수정일시", required = false, example = "")
    private String	udtDt;
	@ApiParam(value = "삭제여부", required = false, example = "")
    private String	delYn;
	@ApiParam(value = "노출여부", required = false, example = "")
    private String	viewYn;
}
