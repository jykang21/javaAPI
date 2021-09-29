package kr.co.softbridge.sobroplatform.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private String	userEmail;
	private String	userId;
	private String	password;
	private String	userName;
	private String	userLevel;
	private int		groupCode;
}
