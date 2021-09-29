package kr.co.softbridge.sobroplatform.meetinglog.dto;

import java.util.List;

import io.swagger.annotations.ApiParam;
import kr.co.softbridge.sobroplatform.file.dto.FileListDto;
import kr.co.softbridge.sobroplatform.file.dto.FileListResponseDto;
import kr.co.softbridge.sobroplatform.room.dto.RoomDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingLogDto {
    private String roomCode;
	
    private String meetingLogSeq;
	
    private String contents;
	
    private String resultCode;
	
    private String resultMsg;

}
