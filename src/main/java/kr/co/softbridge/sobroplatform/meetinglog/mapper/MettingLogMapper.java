package kr.co.softbridge.sobroplatform.meetinglog.mapper;

import java.util.HashMap;

import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;
import kr.co.softbridge.sobroplatform.meetinglog.dto.MeetingLogDto;

@PrimaryMapper
public interface MettingLogMapper {

	MeetingLogDto getMeetingLog(HashMap<String, Object> paramMap);

	void insertMettingLog(HashMap<String, Object> paramMap);

	void updateMettingLog(HashMap<String, Object> paramMap);

	int getMeetingLogCnt(HashMap<String, Object> paramMap);

}
