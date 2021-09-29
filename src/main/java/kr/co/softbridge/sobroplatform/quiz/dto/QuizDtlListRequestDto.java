package kr.co.softbridge.sobroplatform.quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizDtlListRequestDto {

	private String optionNo;
	private String optionContents;
	private String delYn;
	
	@Override
	public String toString() {
		return "QuizDtlListRequestDto [optionNo=" + optionNo + ", optionContents=" + optionContents + ", delYn=" + delYn
				+ "]";
	}
}
