package kr.co.softbridge.sobroplatform.quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizDeleteRequestDto {

	private String roomToken;
	private String roomCode;
	private String userId;
	private String userNm;
	private String quizNo;
	
	private String svcCode;
	private String nowTime;
	@Override
	public String toString() {
		return "QuizDeleteRequestDto [roomToken=" + roomToken + ", roomCode=" + roomCode + ", userId=" + userId
				+ ", userNm=" + userNm + ", quizNo=" + quizNo + "]";
	}
	
}
