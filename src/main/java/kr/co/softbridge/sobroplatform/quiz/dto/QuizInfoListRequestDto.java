package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizInfoListRequestDto {

	private String quizSubNo;
	private String contents;
	private String quizAnswer;
	private String delYn;
	private List<QuizDtlListRequestDto> quizDtlList;
	
	@Override
	public String toString() {
		return "QuizInfoListRequestDto [quizSubNo=" + quizSubNo + ", contents=" + contents + ", quizAnswer="
				+ quizAnswer + ", delYn=" + delYn + ", quizDtlList=" + quizDtlList + "]";
	}
}
