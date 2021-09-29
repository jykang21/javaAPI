package kr.co.softbridge.sobroplatform.sample.service;

import kr.co.softbridge.sobroplatform.commons.dto.TokenReqDto;
import kr.co.softbridge.sobroplatform.commons.dto.UserDto;
import kr.co.softbridge.sobroplatform.sample.dto.BroadListDto;
import kr.co.softbridge.sobroplatform.sample.dto.MockUserDto;
import kr.co.softbridge.sobroplatform.sample.mapper.DataSourceOneMapper;
import kr.co.softbridge.sobroplatform.sample.mapper.DataSourceTwoMapper;
import kr.co.softbridge.sobroplatform.sample.mapper.SampleMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SampleService {

    private static final Logger logger = LogManager.getLogger(SampleService.class);

    private final SampleMapper sampleMapper;
    private final DataSourceOneMapper dataSourceOneMapper;
    private final DataSourceTwoMapper dataSourceTwoMapper;

	public UserDto authenticate(TokenReqDto param) {
		return sampleMapper.getUserInfo(param);
	}

	public List<BroadListDto> findBroadListByBroadCode(String broadCode) {
	    return dataSourceOneMapper.findBroadListByBroadCode(broadCode);
    }

    public List<MockUserDto> fetchMockUsers() {
		return dataSourceTwoMapper.fetchMockUsers();
	}
}
