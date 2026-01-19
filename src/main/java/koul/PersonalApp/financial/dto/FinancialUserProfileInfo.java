package koul.PersonalApp.financial.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.Builder;

/**
 * 재정 유저 프로필 조회 DTO
 * 유저의 재정 정보를 반환할 때 사용
 */
@Builder
public record FinancialUserProfileInfo(
	@JsonRawValue
	String profileData) {
}
