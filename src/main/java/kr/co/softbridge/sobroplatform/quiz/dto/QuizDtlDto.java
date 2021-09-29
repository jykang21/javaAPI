package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizDtlDto {

	private String quizNo;
	private String quizSubNo;
	private String optionNo;
	private String optionContents;
	private String delYn;
	
	@Override
	public String toString() {
		return "QuizDtlListDto [quizNo=" + quizNo + ", quizSubNo=" + quizSubNo + ", optionNo=" + optionNo
				+ ", optionContents=" + optionContents + ", delYn=" + delYn + "]";
	}
}
