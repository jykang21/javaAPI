package kr.co.softbridge.sobroplatform.meetinglog.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiParam;
import kr.co.softbridge.sobroplatform.room.dto.RoomDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class MeetingLogRequestDto {

	private String saveType = "C";
    
    private String roomCode;

    private String contents;

    private String roomToken;
    
    private String svcCode;
    
    private String userId;
    
    private String userNm;

}
