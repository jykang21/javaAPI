package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizUserResponseDto {
	
	private String userNm;
	
	@Override
	public String toString() {
		return "QuizUserResponseDto [userNm=" + userNm + "]";
	}

}
