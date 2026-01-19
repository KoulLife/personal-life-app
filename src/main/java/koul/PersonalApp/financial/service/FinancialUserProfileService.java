package koul.PersonalApp.financial.service;

import koul.PersonalApp.financial.dto.FinancialUserProfileCommand;
import koul.PersonalApp.financial.dto.FinancialUserProfileInfo;

public interface FinancialUserProfileService {

	/**
	 * 유저의 재정 프로필 설정
	 */
	void setFinancialUserProfile(Long userId, FinancialUserProfileCommand command);

	/**
	 * 유저의 재정 프로필 조회
	 */
	FinancialUserProfileInfo getFinancialUserProfileInfo(Long userId);

}
