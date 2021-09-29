package kr.co.softbridge.sobroplatform.sample.mapper;

import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;
import kr.co.softbridge.sobroplatform.sample.dto.BroadListDto;

import java.util.List;

@PrimaryMapper
public interface DataSourceOneMapper {

    List<BroadListDto> findBroadListByBroadCode(String broadCode);

}
