package kr.co.softbridge.sobroplatform.quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizChangeRequestDto {

	private String roomToken;
	private String roomCode;
	private String userId;
	private String userNm;
	
	private String quizNo;
	private String quizChange;
	
	private String svcCode;
	private String nowTime;
	
	@Override
	public String toString() {
		return "QuizChangeRequestDto [roomToken=" + roomToken + ", roomCode=" + roomCode + ", userId=" + userId
				+ ", userNm=" + userNm + "]";
	}
	
}
