package kr.co.softbridge.sobroplatform.file.mapper;

import kr.co.softbridge.sobroplatform.commons.annotation.SecondaryMapper;
import kr.co.softbridge.sobroplatform.file.dto.*;

import java.util.HashMap;
import java.util.List;

/**
 * 파일 mapper
 */
@SecondaryMapper
public interface FileMapper {

    /**
     * 다음 파일 시퀀스 조회
     * @param room_code
     * @return 다음 파일 시퀀스
     */
    Integer getNextSequenceByRoomCode(String room_code);

    /**
     * 파일 마스터 저장
     * @param fileMaster
     * @return
     */
    int saveFileMaster(FileUploadRequestDto fileManager);
    
    /**
     * 파일 매니저 저장
     * @param fileManager
     * @return
     */
    int saveFileManager(FileUploadRequestDto fileManager);
    
    /**
     * 파일 공유그룹 저장
     * @param shareGroup
     * @return
     */
    int saveFileShareGroup(FileUploadRequestDto fileManager);
    
    /**
     * 파일 공유대상 저장
     * @param fileShareTarget
     * @return
     */
    int saveFileShareTarget(FileUploadRequestDto fileManager);

    /**
     * 파일 삭제
     * @param fileDeleteRequestDto
     * @return
     */
    int deleteFile(FileDeleteRequestDto fileDeleteRequestDto);

    /**
     * 삭제한 파일 조회
     * @param fileManagerDto
     * @return
     */
    FileDeleteResponseDto getFileByIdToDeleteFile(FileManagerDto fileManagerDto);

    /**
     * 파일 목록 조회
     * @param fileLookUpRequestDto
     * @return
     */
	List<FileListDto> getFileList(HashMap<String, Object> paramMap);

	void deleteShareTargetFile(FileDeleteRequestDto fileDeleteRequestDto);

	void deleteShareGroupFile(FileDeleteRequestDto fileDeleteRequestDto);

	void deleteMasterFile(FileDeleteRequestDto fileDeleteRequestDto);
	
	/* 파일 리스트 토탈 카운트 */
	int getFileListCount(HashMap<String, Object> paramMap);
}
