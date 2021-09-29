package kr.co.softbridge.sobroplatform.quiz.dto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizCreateResponseDto {

	@ApiParam(value = "응답 코드", required = true, example = "000000")
    private String resultCode;
	@ApiParam(value = "응답 메시지", required = true, example = "성공")
    private String resultMsg;
	@ApiParam(value = "퀴즈 번호", required = true, example = "1")
    private String quizNo;
	
	@Override
	public String toString() {
		return "QuizCommonResponseDto [resultCode=" + resultCode + ", resultMsg=" + resultMsg + "]";
	}
}
