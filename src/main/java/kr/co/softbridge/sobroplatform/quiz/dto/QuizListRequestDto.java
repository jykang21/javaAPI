package kr.co.softbridge.sobroplatform.quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizListRequestDto {

	private String roomToken;
	private String roomCode;
	private String userId;
	private String userNm;
//	private String managerAuth;
	private String presenterYn;
	
	private String svcCode;
	
	@Override
	public String toString() {
		return "QuizListRequestDto [roomToken=" + roomToken + ", roomCode=" + roomCode + ", userId=" + userId
				+ ", userNm=" + userNm + ", presenterYn=" + presenterYn + "]";
	}
	
}
