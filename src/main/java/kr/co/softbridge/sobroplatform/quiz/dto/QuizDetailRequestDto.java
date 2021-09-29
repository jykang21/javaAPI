package kr.co.softbridge.sobroplatform.quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizDetailRequestDto {

	private String roomToken;
	private String roomCode;
	private String userId;
	private String userNm;
	
	private String quizNo;
	private String status;
	
	private String svcCode;
	
	@Override
	public String toString() {
		return "QuizDetailRequestDto [roomToken=" + roomToken + ", roomCode=" + roomCode + ", userId=" + userId
				+ ", userNm=" + userNm + ", quizNo=" + quizNo + "]";
	}
	
}
