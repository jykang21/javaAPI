package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizUserRequestDto {
	
	private String quizNo;
	private String quizSubNo;
	private String userNm;
	private String userStatus;
	private String submitAnswer;
	
	@Override
	public String toString() {
		return "QuizUserResponseDto [userNm=" + userNm + "]";
	}

}
