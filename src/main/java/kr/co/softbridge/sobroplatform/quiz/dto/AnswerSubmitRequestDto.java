package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerSubmitRequestDto {

	private String roomToken;
	private String roomCode;
	private String userId;
	private String userNm;
	
	private String quizNo;
	private List<QuizUserRequestDto> quizAnswerList;
	
	private String svcCode;
	
	@Override
	public String toString() {
		return "AnswerSubmitRequestDto [roomToken=" + roomToken + ", roomCode=" + roomCode + ", userId=" + userId
				+ ", userNm=" + userNm + ", quizNo=" + quizNo + ", quizAnswerList=" + quizAnswerList + "]";
	}
	
}
