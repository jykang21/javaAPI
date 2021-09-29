package kr.co.softbridge.sobroplatform.room.mapper;

import java.util.HashMap;
import java.util.List;

import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;
import kr.co.softbridge.sobroplatform.commons.dto.MonitoringRoomLogDto;
import kr.co.softbridge.sobroplatform.commons.dto.TokenRoomDto;
import kr.co.softbridge.sobroplatform.room.dto.RoomCheckDto;
import kr.co.softbridge.sobroplatform.room.dto.RoomDto;

@PrimaryMapper
public interface RoomMapper {

	List<RoomDto> getRoomList(HashMap<String, Object> paramMap);

	int insertRoom(HashMap<String, Object> paramMap);

	int updateRoom(HashMap<String, Object> paramMap);

	String getRoomCode();

	int checkUpdateRoomUser(HashMap<String, Object> paramMap);

	MonitoringRoomLogDto getRoomLogRow(HashMap<String, Object> paramMap);

	TokenRoomDto checkRoomCnt(HashMap<String, Object> dbMap);
	
	String checkRoomManagerId(HashMap<String, Object> dbMap);

	int updateRoomStatus(HashMap<String, Object> paramMap);

	RoomCheckDto getRoomRow(HashMap<String, Object> paramMap);

	int deleteRoom(HashMap<String, Object> delParam);

	void deleteRoomUser(HashMap<String, Object> delParam);

	/* room total count */
	int getRoomTotalCount(HashMap<String, Object> paramMap);
}

