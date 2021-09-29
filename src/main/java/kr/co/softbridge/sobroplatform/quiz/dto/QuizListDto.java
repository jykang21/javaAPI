package kr.co.softbridge.sobroplatform.quiz.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizListDto {

	private String quizNo;
	private String title;
	private String status;
	private String startDt;
	private String runningTime;
	
	@Override
	public String toString() {
		return "QuizInfoListDto [quizNo=" + quizNo + ", title=" + title + ", status=" + status + ", startDt=" + startDt
				+ ", runningTime=" + runningTime + "]";
	}
}
