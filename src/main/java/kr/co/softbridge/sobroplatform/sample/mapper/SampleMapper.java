package kr.co.softbridge.sobroplatform.sample.mapper;

import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;
import kr.co.softbridge.sobroplatform.commons.dto.TokenReqDto;
import kr.co.softbridge.sobroplatform.commons.dto.UserDto;

@PrimaryMapper
public interface SampleMapper {

	UserDto getUserInfo(TokenReqDto param);

}
