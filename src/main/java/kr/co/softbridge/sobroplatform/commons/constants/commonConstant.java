package kr.co.softbridge.sobroplatform.commons.constants;

public class commonConstant {
	// 사용자 권한
	public static String SERVICE_ADMIN				= "A";			// 관리자
	public static String SERVICE_MANAGER			= "M";			// 방송매니저
	public static String SERVICE_USER				= "U";			// 사용자
	
	public static String SERVICE_NAME				= "다자간 영상회의";
	
	// 회의 접속 사용자 LEVEL
	public static String LEVEL_MANAGER				= "1001";		// 매니저
	public static String LEVEL_USER					= "2001";		// 사용자
	
	/* 회의리스트 코드상수 */
	// 방송타입
	public static String ROOM_TYPE_DEFAULT			= "01";			// 영상회의
	public static String ROOM_TYPE_M_TO_N			= "02";			// M:N
	public static String ROOM_TYPE_ONE_TO_N			= "03";			// 1:N
	
	// 방송상태
	public static String ROOM_STATUS_WAITING		= "01";			// 대기
	public static String ROOM_STATUS_RUNNING		= "02";			// 진행중
	public static String ROOM_STATUS_CLOSE			= "03";			// 종료
	
	// 방송 품질
	public static String QUALITY_DEFAULT			= "00";			// 참여자수
	public static String QUALITY_128				= "01";			// 128kb
	public static String QUALITY_256				= "02";			// 256kb
	public static String QUALITY_512				= "03";			// 512kb
	public static String QUALITY_1024				= "04";			// 1024kb
	public static String QUALITY_2048				= "05";			// 2048kb
	public static String QUALITY_4096				= "06";			// 4096kb
	public static String QUALITY_8192				= "07";			// 8192kb
	
	// 녹화타입
	public static String REC_TYPE_PRESENTER_ONLY	= "01";			// 발표자만
	public static String REC_TYPE_PARTICIPANTS		= "02";			// 참여자포함
	
	// 저장타입
	public static String SAVE_CRATE					 = "C";			// 등록
	public static String SAVE_UPDATE				 = "U";			// 수정
	public static String SAVE_DELETE				 = "D";			// 삭제
	
	/*
	 * 에러메세지 처리
	 */
	public static String M_000000					= "성공";
	public static String M_000001					= "필수 정보 누락";
	public static String M_000002					= "DB 저장 실패";
	public static String M_000003					= "일시적으로 시스템을 사용할 수 없습니다. 잠시 후 다시 시도해주세요.";
	public static String M_000004					= "올바른 입력값이 아닙니다.";
	
	public static String M_001001					= "회의 정보가 없습니다.";
	public static String M_001002					= "회의 참석 인원을 초과하였습니다.";
	public static String M_001003					= "회의 참여에 실패했습니다.";
	public static String M_001004					= "회의 정보 업데이트에 실패하였습니다.";
	public static String M_001005					= "이미 존재하는 사용자명입니다.";
	public static String M_001006					= "사용자 정보를 찾을 수 없습니다.";
	public static String M_001007					= "사용자 정보 업데이트에 실패하였습니다.";
	public static String M_001008					= "회의 생성에 실패하였습니다.";
	public static String M_001009					= "회의 입장 비밀번호를 확인해 주세요.";
	public static String M_001010					= "진행 중인 회의가 아닙니다.";
	public static String M_001011					= "종료된 회의입니다.";
	public static String M_001012					= "상태변경을 할 수 없는 회의입니다.";
	public static String M_001013					= "닉네임은 최대 한글 1~10자, 영문 및 숫자 2~20자 입니다.";
	
	public static String M_002001					= "토큰 생성에 실패했습니다.";
	public static String M_002002					= "토큰정보 업데이트에 실패했습니다.";
	public static String M_002003					= "토큰 정보가 잘못되었습니다.";
	public static String M_002004					= "토큰이 유효하지 않습니다.";
	public static String M_002005					= "토큰 생성 정보가 잘못되었습니다.";
	public static String M_002006					= "토큰 만료시간이 경과하였습니다.";
	
	public static String M_003001					= "수정 권한 없음";
	public static String M_003002					= "삭제 권한 없음";
	public static String M_003003					= "권한 없음";
	
	public static String M_004001					= "파일이 존재하지 않습니다.";
	
	public static String M_005001					= "이벤트 퀴즈의 생성에 실패하였습니다.";
	public static String M_005002					= "이벤트 퀴즈의 수정에 실패하였습니다.";
	public static String M_005003					= "이벤트 퀴즈의 삭제에 실패하였습니다.";
	public static String M_005004					= "이벤트 퀴즈를 시작할 수 없습니다.";
	public static String M_005005					= "답안 제출에 실패하였습니다.";
	public static String M_005006					= "이벤트 퀴즈가 존재하지 않습니다.";
	public static String M_005007					= "미발행 퀴즈만 수정할수 있습니다.";
	public static String M_005008					= "제한시간은 숫자만 입력 가능합니다.";
	
	
}
