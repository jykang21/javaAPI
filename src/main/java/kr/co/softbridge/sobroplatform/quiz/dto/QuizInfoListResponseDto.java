package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizInfoListResponseDto {

	private String quizSubNo;
	private String contents;
	private String quizAnswer;
	private String submitAnswerYn;
	private String mySubmitAnwer;
	private List<QuizDtlListResponseDto> quizDtlList;
	
	@Override
	public String toString() {
		return "QuizInfoListResponseDto [quizSubNo=" + quizSubNo + ", contents=" + contents + ", quizAnswer="
				+ quizAnswer + ", quizDtlList=" + quizDtlList + "]";
	}
}
