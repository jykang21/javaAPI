package kr.co.softbridge.sobroplatform.quiz.mapper;

import java.util.HashMap;
import java.util.List;

import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizDtlDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizInfoQueryDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizListDto;
import kr.co.softbridge.sobroplatform.quiz.dto.QuizUserDto;

@PrimaryMapper
public interface QuizMapper {
	
	/* quizNo 조회 */
	String getQuizNo() throws Exception;

	/* quizInfo 저장 */
	int insertQuizInfo(HashMap<String, Object> paramMap) throws Exception;
	
	/* quizDtl 저장 */
	int insertQuizDtl(HashMap<String, Object> paramMap) throws Exception;

	/* quizInfo 리스트 조회 */
	List<QuizListDto> getQuizInfoList(HashMap<String, Object> paramMap) throws Exception;

	/* quizInfo count */
	int getQuizInfoCount(HashMap<String, Object> paramMap) throws Exception;
	
	/* quizInfo 수정 */
	int updateQuizInfo(HashMap<String, Object> paramMap) throws Exception;

	/* quizDtl count */
	int getQuizDtlCount(HashMap<String, Object> paramMap) throws Exception;
	
	/* quizDtl 수정 */
	int updateQuizDtl(HashMap<String, Object> paramMap) throws Exception;
	
	/* quizInfo delYn 수정 */
	int updateQuizInfoDelYn(HashMap<String, Object> paramMap) throws Exception;

	/* quizDtl delYn 수정 */
	int updateQuizDtlDelYn(HashMap<String, Object> paramMap) throws Exception;

	/* quizUser 저장 */
	int insertQuizUser(HashMap<String, Object> paramMap) throws Exception;

	/* quizInfo 조회 */
	List<QuizInfoQueryDto> getQuizInfo(HashMap<String, Object> paramMap) throws Exception;

	/* quizDtl 조회 */
	List<QuizDtlDto> getQuizDtl(HashMap<String, Object> paramMap) throws Exception;

	/* quizUser 조회 */
	List<QuizUserDto> getQuizUser(HashMap<String, Object> paramMap) throws Exception;

	/* total quizUser Count 조회 */
	int getQuizUserCount(HashMap<String, Object> dbMap) throws Exception;

	/* quizInfo 삭제 */
	int deleteQuizInfo(HashMap<String, Object> dbMap) throws Exception;

	/* quizDtl 삭제 */
	int deleteQuizDtl(HashMap<String, Object> dbMap) throws Exception;


}
