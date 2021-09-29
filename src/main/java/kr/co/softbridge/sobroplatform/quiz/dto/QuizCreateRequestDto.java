package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizCreateRequestDto {

	private String roomToken;
	private String roomCode;
	private String userId;
	private String userNm;
	private String title;
	private String runningTime;
	private List<QuizInfoListResponseDto> quizInfoList;
	private String svcCode;
	private String nowTime;
	
	@Override
	public String toString() {
		return "QuizCreateRequestDto [roomToken=" + roomToken + ", roomCode=" + roomCode + ", userId=" + userId
				+ ", userNm=" + userNm + ", title=" + title + ", runningTime=" + runningTime + ", quizInfoList="
				+ quizInfoList + "]";
	}
	
}
