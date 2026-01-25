package koul.PersonalApp.financial.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import koul.PersonalApp.financial.dto.FinancialUserProfileCommand;
import koul.PersonalApp.financial.dto.FinancialUserProfileInfo;
import koul.PersonalApp.financial.entity.FinancialUserProfile;
import koul.PersonalApp.financial.repository.FinancialUserProfileRepository;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * 재정 유저 프로필 서비스 구현체
 * 유저의 재정 관련 개인정보 관리
 */
@Service
@RequiredArgsConstructor
public class FinancialUserProfileServiceImpl implements FinancialUserProfileService {

	private final FinancialUserProfileRepository financialUserProfileRepository;
	private final UserRepository userRepository;

	/**
	 * 유저의 재정 프로필 설정
	 * 기존 프로필이 있으면 업데이트, 없으면 신규 생성
	 */
	@Override
	@Transactional
	public void setFinancialUserProfile(Long userId, FinancialUserProfileCommand command) {
		financialUserProfileRepository.findByUser_UserId(userId)
			.ifPresentOrElse(
				// 프로필이 존재하면 업데이트
				profile -> profile.updateProfileData(command.profileData()),

				// 프로필이 없으면 신규 생성
				() -> {
					User user = userRepository.findById(userId)
						.orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

					FinancialUserProfile newProfile = FinancialUserProfile.builder()
						.user(user)
						.profileData(command.profileData())
						.build();
					financialUserProfileRepository.save(newProfile);
				}
			);
	}

	/**
	 * 유저의 재정 프로필 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public FinancialUserProfileInfo getFinancialUserProfileInfo(Long userId) {
		userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

		return financialUserProfileRepository.findByUser_UserId(userId)
				.map(profile -> FinancialUserProfileInfo.builder()
					.profileData(profile.getProfileData())
					.build())
				.orElse(null);
	}
}
