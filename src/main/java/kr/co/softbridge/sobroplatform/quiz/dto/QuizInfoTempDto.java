package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizInfoTempDto {

	private String quizSubNo;
	private String contents;
	private String quizAnswer;
	private List<QuizDtlDto> dtlList;
	
	@Override
	public String toString() {
		return "QuizInfoTempDto [quizSubNo=" + quizSubNo + ", contents=" + contents + ", quizAnswer=" + quizAnswer
				+ "]";
	}
}
