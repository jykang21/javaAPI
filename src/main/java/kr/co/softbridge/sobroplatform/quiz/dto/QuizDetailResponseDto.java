package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizDetailResponseDto {

	@ApiParam(value = "응답 코드", required = true, example = "000000")
    private String resultCode;
	@ApiParam(value = "응답 메시지", required = true, example = "성공")
    private String resultMsg;
	private String quizNo;
	private String title;
	private String status;
	private String startDt;
	private String runningTime;
	
	private List<QuizInfoListResponseDto> quizInfoList;
	
	@Override
	public String toString() {
		return "QuizListResponseDto [resultCode=" + resultCode + ", resultMsg=" + resultMsg + ", quizNo=" + quizNo
				+ ", title=" + title + ", status=" + status + ", startDt=" + startDt + ", runningTime=" + runningTime
				+ "]";
	}
}
