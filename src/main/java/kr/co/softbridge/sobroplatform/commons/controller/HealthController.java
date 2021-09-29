package kr.co.softbridge.sobroplatform.commons.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.softbridge.sobroplatform.commons.dto.HealthResDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("")
@RestController
public class HealthController {

	@ApiOperation(value = "서비스상태확인", notes = "현재 서버의 상태를 확인한다.")
	@GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HealthResDto> healthCheck() {
		HealthResDto healthResDto = new HealthResDto();
		healthResDto.setStatus(200);
		return ResponseEntity.ok().body(healthResDto);
	}
}
