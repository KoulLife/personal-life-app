package koul.PersonalApp.financial.dto;

import lombok.Builder;

/**
 * 재정 유저 프로필 설정 커맨드
 * 유저의 재정 정보 (나이, 직업, 가족 구성 등)을 설정할 때 사용
 */
@Builder
public record FinancialUserProfileCommand(
	String profileData) {
}
