package kr.co.softbridge.sobroplatform.sample.mapper;

import kr.co.softbridge.sobroplatform.commons.annotation.SecondaryMapper;
import kr.co.softbridge.sobroplatform.sample.dto.MockUserDto;

import java.util.List;

@SecondaryMapper
public interface DataSourceTwoMapper {

    List<MockUserDto> fetchMockUsers();

}
