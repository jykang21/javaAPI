package kr.co.softbridge.sobroplatform.sample.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BroadListDto {

    private String svcCode;
    private String roomCode;
    private String managerId;
    private String managerNick;
    private Long maxPeople;
    private String title;
    private String quality;
    private String startDt;
    private String endDt;
    private String viewYn;
    private String roomPw;
    private String roomStatus;
    private String recType;

}
