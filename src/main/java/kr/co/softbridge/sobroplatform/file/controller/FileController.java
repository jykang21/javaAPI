package kr.co.softbridge.sobroplatform.file.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.softbridge.sobroplatform.commons.dto.CommonResDto;
import kr.co.softbridge.sobroplatform.commons.service.TloLogService;
import kr.co.softbridge.sobroplatform.file.dto.FileDeleteRequestDto;
import kr.co.softbridge.sobroplatform.file.dto.FileDeleteResponseDto;
import kr.co.softbridge.sobroplatform.file.dto.FileDownloadRequestDto;
import kr.co.softbridge.sobroplatform.file.dto.FileListResponseDto;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadRequestDto;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadResponseDto;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadWhiteboardRequestDto;
import kr.co.softbridge.sobroplatform.file.dto.FileUploadWhiteboardResponseDto;
import kr.co.softbridge.sobroplatform.file.service.FileDeleteService;
import kr.co.softbridge.sobroplatform.file.service.FileDownloadService;
import kr.co.softbridge.sobroplatform.file.service.FileService;
import kr.co.softbridge.sobroplatform.file.service.FileUploadService;

/**
 * 파일 controller
 */
@RestController
@RequestMapping("/files")
public class FileController {

	private static final Logger logger = LogManager.getLogger(FileController.class);
	
    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private FileDeleteService fileDeleteService;
    
    @Autowired
    private TloLogService tloLogService;

    /**
     * 파일 조회 처리
     * @param dto
     * @return
     */
	@ApiOperation(value = "파일목록조회", notes = "등록된 파일의 목록을 조회")
    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileListResponseDto> filesList(
    		HttpServletRequest request, @RequestBody(required = true) HashMap<String, Object> paramMap) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
    	ResponseEntity<FileListResponseDto> result = fileService.getFilesList(request, paramMap);
    	
    	String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010401", result.getBody().getResultCode(), paramMap);
        return result;
    }
    
    /**
     * 파일 업로드 요청 처리
     * @param fileManagerDTO
     * @return
     * @throws IOException
     */
	@ApiOperation(value = "파일업로드", notes = "파일을 S3에 업로드한다.")
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileUploadResponseDto> uploadFile(HttpServletRequest request, @Valid FileUploadRequestDto fileManagerDTO) throws IOException {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
    	ResponseEntity<FileUploadResponseDto> result = fileUploadService.uploadFile(request, fileManagerDTO);
    	
    	/* 통합로그를 위해 paramMap에 담음 */
    	HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcCode", fileManagerDTO.getSvcCode());
		paramMap.put("roomCode", fileManagerDTO.getRoomCode());
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010402", result.getBody().getResultCode(), paramMap);
        return result;
    }

    /**
     * 파일 다운로드 처리
     * @param file_path
     * @return
     * @throws IOException 
     */
    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request, @Valid FileDownloadRequestDto fileDownloadRequestDto) throws Exception {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
    	ResponseEntity<byte[]> result = fileDownloadService.downloadFile(request, fileDownloadRequestDto);
    	
    	/* 통합로그를 위해 paramMap에 담음 */
    	HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcCode", fileDownloadRequestDto.getSvcCode());
		paramMap.put("roomCode", fileDownloadRequestDto.getRoomCode());
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	String resultCode = "000000";
    	if(!HttpStatus.OK.equals(result.getStatusCode())) {
    		resultCode = "000003";
    	}
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010403", resultCode, paramMap);
        return result;
    }

    /**
     * 파일 삭제 처리
     * @param fileDeleteRequestDto
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntity<CommonResDto> deleteFile(HttpServletRequest request, @Valid FileDeleteRequestDto fileDeleteRequestDto) {
    	String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
    	ResponseEntity<CommonResDto> result = fileDeleteService.deleteFile(request, fileDeleteRequestDto); 
    	
    	/* 통합로그를 위해 paramMap에 담음 */
    	HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcCode", fileDeleteRequestDto.getSvcCode());
		paramMap.put("roomCode", fileDeleteRequestDto.getRoomCode());
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010404", result.getBody().getResultCode(), paramMap);
        return result;
    }
    
    /**
     * 화이트보드 파일 업로드 요청 처리
     * @param fileManagerDTO
     * @return
     * @throws IOException
     */
	@ApiOperation(value = "화이트보드 파일업로드", notes = "화이트보드 파일을 S3에 업로드한다.")
    @PostMapping(value = "/whiteboardUpload", produces = MediaType.APPLICATION_JSON_VALUE)
    public FileUploadWhiteboardResponseDto whiteboardUploadFile(HttpServletRequest request, @RequestBody FileUploadWhiteboardRequestDto fileManagerDTO) throws IOException {
		String pattern = "yyyy-MM-dd HH:mm:ss";
    	String pattern2 = "yyyyMMddHHmmssSSS";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
    	String startServerTime = simpleDateFormat.format(new Date());
    	String startServerTime2 = simpleDateFormat2.format(new Date());
    	
    	logger.info(fileManagerDTO.getRoomCode());
    	
    	FileUploadWhiteboardResponseDto result = fileUploadService.whiteboardUploadFile(request, fileManagerDTO);
    	
    	/* 통합로그를 위해 paramMap에 담음 */
    	HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("svcCode", fileManagerDTO.getSvcCode());
		paramMap.put("roomCode", fileManagerDTO.getRoomCode());
		String endServerTime = simpleDateFormat.format(new Date());
    	String endServerTime2 = simpleDateFormat2.format(new Date());
    	tloLogService.tloLog(request, startServerTime2, endServerTime2, "S010405", result.getResultCode(), paramMap);
        return result;
    }
}
