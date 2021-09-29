package kr.co.softbridge.sobroplatform.login.mapper;

import java.util.HashMap;
import java.util.Map;

import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;

import kr.co.softbridge.sobroplatform.commons.dto.RoomDto;
import kr.co.softbridge.sobroplatform.commons.dto.SvcInfoDto;
import kr.co.softbridge.sobroplatform.commons.dto.TokenResDto;
import kr.co.softbridge.sobroplatform.login.dto.LiveUserDto;

@PrimaryMapper
public interface LoginMapper {

	LiveUserDto checkRoomUser(Map<String, Object> paramMap);

	RoomDto getRoom(Map<String, Object> paramMap);

	int insertRoomUser(Map<String, Object> paramMap);

	String getUserId();

	LiveUserDto getRoomUser(Map<String, Object> paramMap);

	int updateTokenExp(Map<String, Object> paramMap);

	int updateRoomUserTokenIdx(Map<String, Object> paramMap);

	TokenResDto getTokenInfo(Map<String, Object> paramMap);

	int updateRoomUserEndDate(Map<String, Object> paramMap);

	int roomPeople(Map<String, Object> dbMap);

	int getSvcInfoCnt(Map<String, Object> dbMap);

	SvcInfoDto getSvcInfo(Map<String, Object> dbMap);
	
	int getRoomUserCount(HashMap<String, Object> dbMap);

}
