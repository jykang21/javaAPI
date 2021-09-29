package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizDtlListResponseDto {

	private String optionNo;
	private String optionContents;
	private String selectUserPercent;
	private String selectUserList;
	
	@Override
	public String toString() {
		return "QuizDtlListResponseDto [optionNo=" + optionNo + ", optionContents=" + optionContents + "]";
	}
}
