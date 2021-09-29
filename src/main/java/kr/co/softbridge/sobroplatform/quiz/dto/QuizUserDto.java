package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizUserDto {

	private String quizNo;
	private String quizSubNo;
	private String userId;
	private String userNm;
	private String userStatus;
	private String submitAnswer;
	
	@Override
	public String toString() {
		return "QuizUserDto [quizNo=" + quizNo + ", quizSubNo=" + quizSubNo + ", userId=" + userId + ", userNm="
				+ userNm + ", userStatus=" + userStatus + ", submitAnswer=" + submitAnswer + "]";
	}
}
