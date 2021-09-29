package kr.co.softbridge.sobroplatform;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@AutoConfigureMockMvc
@SpringBootTest
public class ApiJunitTests {
	
	private static final Logger logger = LogManager.getLogger(ApiJunitTests.class);
	
	/* 필수 작성 코드 Start */
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	private String svcCode = "rumOWCvxRdpswCdLJI6Y";
	
	@BeforeAll
	static void beforeAll() {
	}
	
	@BeforeEach
	public void beforeEach() {

		objectMapper = Jackson2ObjectMapperBuilder.json()
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.modules(new JavaTimeModule())
				.build();
	}
	
	public void postResult(String url, String content) throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(result -> {
					MockHttpServletResponse response = result.getResponse();
					if(response.getStatus() == 200) {
						status().isOk();
					} else if(response.getStatus() == 201) {
						status().isCreated();
					} else {
						status().isNoContent();
					}
					logger.info(response.getContentAsString());
				});
	}
	
	public void getResult(String url, String content) throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(result -> {
					MockHttpServletResponse response = result.getResponse();
					if(response.getStatus() == 200) {
						status().isOk();
					} else if(response.getStatus() == 201) {
						status().isCreated();
					} else {
						status().isNoContent();
					}
					logger.info("response ::: ", response);
				});
	}
	/* 필수 작성 코드 End */
	
	// health 체크 테스트
	@Test
	public void gethealth() throws Exception{
		String url = "/health";
		
		getResult(url, "");
	}

	// 공통코드 체크 테스트
	@Test
	public void getCodeList() throws Exception{
		String url = "/common/list";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("grpCode", "01");
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 실시간 채널 Log 체크 테스트
	@Test
	public void realTimeLog() throws Exception{
		String url = "/common/realTimeLog";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcCode",			"0");
		paramMap.put("roomCode",		"0");
		paramMap.put("userId",			"0");
		paramMap.put("videoBitrate",	"0");
		paramMap.put("videoWidth",		"0");
		paramMap.put("videoHeight",		"0");
		paramMap.put("videoFrameRate",	"0");
		paramMap.put("videoAspectRatio","0");
		paramMap.put("audioBitrate",	"0");
		paramMap.put("audioLaency",		"0");
		paramMap.put("audioSampleRate",	"0");
		paramMap.put("audioSampleSize",	"0");
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 실시간 채널 목록 체크 테스트
	@Test
	public void realTimeRoomList() throws Exception{
		String url = "/common/realTimeRoomList";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomCode", "10");
		paramMap.put("svcCode", svcCode);
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 토큰 생성 체크 테스트
	@Test
	public void auth() throws Exception{
		String url = "/login/auth";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcDomain", "https://localhost:8080");
		paramMap.put("svcCode", svcCode);
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 서비스토큰검증 테스트
	@Test
	public void roomVerify() throws Exception{
		String url = "/login/auth/roomVerify";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomToken", "abcdefg");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// Room토큰검증 테스트
	@Test
	public void svcVerify() throws Exception{
		String url = "/login/auth/svcVerify";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "abcdefg");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 회의입장 체크 테스트
	@Test
	public void roomJoin() throws Exception{
		String url = "/login/roomJoin";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomCode", "10");
		paramMap.put("userNm", "김엘지");
		paramMap.put("roomPw", "1234");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 회의종료 체크 테스트
	@Test
	public void roomOut() throws Exception{
		String url = "/login/roomOut";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "1234");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 회의목록 체크 테스트
	@Test
	public void roomList() throws Exception{
		String url = "/room/list";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomType", "01");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 회의등록 체크 테스트
	@Test
	public void roomCreate() throws Exception{
		String url = "/room/create";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomType", "01");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 방수정 체크 테스트
	@Test
	public void roomUpdate() throws Exception{
		String url = "/room/update";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomType", "01");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 방삭제 체크 테스트
	@Test
	public void roomDelete() throws Exception{
		String url = "/room/delete";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomType", "01");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 바로시작 체크 테스트
	@Test
	public void fastStart() throws Exception{
		String url = "/room/fastStart";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "01");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 회의재시작 체크 테스트
	@Test
	public void reStartStatus() throws Exception{
		String url = "/room/reStartStatus";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "streamer1");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 파일목록 체크 테스트
	@Test
	public void fileList() throws Exception{
		String url = "/file/list";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 파일업로드 체크 테스트
	@Test
	public void fileUpload() throws Exception{
		String url = "/file/upload";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 파일다운로드 체크 테스트
	@Test
	public void fileDownload() throws Exception{
		String url = "/file/download";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 파일삭제 체크 테스트
	@Test
	public void fileDelete() throws Exception{
		String url = "/file/delete";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 화이트보드 파일업로드 체크 테스트
	@Test
	public void whiteBoardUpload() throws Exception{
		String url = "/file/whiteBoardUpload";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 회의록 저장 체크 테스트
	@Test
	public void meetingLogSave() throws Exception{
		String url = "/meetinglog/save";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 회의록 보기 체크 테스트
	@Test
	public void meetingLogView() throws Exception{
		String url = "/meetinglog/view";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("roomToken", "abcd");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 퀴즈생성 체크 테스트
	@Test
	public void quizCreate() throws Exception{
		String url = "/quiz/quizCreate";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "streamer1");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 퀴즈수정 체크 테스트
	@Test
	public void quizUpdate() throws Exception{
		String url = "/quiz/quizUpdate";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "streamer1");
		paramMap.put("quizNo", "1");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 퀴즈삭제 체크 테스트
	@Test
	public void quizDelete() throws Exception{
		String url = "/quiz/quizDelete";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "streamer1");
		paramMap.put("quizNo", "1");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 퀴즈목록 체크 테스트
	@Test
	public void quizList() throws Exception{
		String url = "/quiz/quizList";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "streamer1");
		paramMap.put("quizNo", "1");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 퀴즈상세 체크 테스트
	@Test
	public void quizDetail() throws Exception{
		String url = "/quiz/quizDetail";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "streamer1");
		paramMap.put("quizNo", "1");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 퀴즈상태변경 체크 테스트
	@Test
	public void quizChange() throws Exception{
		String url = "/quiz/quizChange";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "streamer1");
		paramMap.put("quizNo", "1");
		paramMap.put("quizChange", "123");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}

	// 퀴즈답안제출 체크 테스트
	@Test
	public void answerSubmit() throws Exception{
		String url = "/quiz/answerSubmit";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roomToken", "efg");
		paramMap.put("roomCode", "10");
		paramMap.put("userId", "streamer1");
		paramMap.put("quizNo", "1");
		paramMap.put("quizChange", "123");
		
		
		String content = objectMapper.writeValueAsString(paramMap);
		logger.info(content);

		postResult(url, content);
	}
}
