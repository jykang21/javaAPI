package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizInfoQueryDto {

	private String quizNo;
	private String quizSubNo;
	private String title;
	private String contents;
	private String status;
	private String startDt;
	private String runningTime;
	private String quizAnswer;
	private String delYn;
	private String regId;
	
	@Override
	public String toString() {
		return "QuizInfoListDto [quizNo=" + quizNo + ", title=" + title + ", status=" + status + ", startDt=" + startDt
				+ ", runningTime=" + runningTime + "]";
	}
}
