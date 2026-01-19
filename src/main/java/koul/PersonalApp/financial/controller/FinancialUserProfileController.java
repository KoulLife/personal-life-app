package koul.PersonalApp.financial.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import koul.PersonalApp.financial.dto.FinancialUserProfileCommand;
import koul.PersonalApp.financial.dto.FinancialUserProfileInfo;
import koul.PersonalApp.financial.service.FinancialUserProfileService;
import koul.PersonalApp.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

/**
 * 재정 유저 프로필 컨트롤러
 * 유저의 재정 관련 개인정보 (나이, 직업, 가구 구성 등) 설정 및 조회 API
 */
@RestController
@RequestMapping("/financial-profile")
@RequiredArgsConstructor
public class FinancialUserProfileController {

	private final FinancialUserProfileService financialUserProfileService;

	/**
	 * 재정 유저 프로필 설정
	 * 기존 프로필이 있으면 업데이트, 없으면 신규 생성 (Upsert)
	 */
	@PostMapping
	public ResponseEntity<String> setFinancialUserProfile(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody String request) {
		FinancialUserProfileCommand command = FinancialUserProfileCommand.builder()
			.profileData(request)
			.build();

		financialUserProfileService.setFinancialUserProfile(userDetails.getUserId(), command);
		return ResponseEntity.ok("재정 프로필이 성공적으로 저장되었습니다.");
	}

	/**
	 * 재정 유저 프로필 조회
	 * 유저의 재정 관련 개인정보를 JSON 형태로 반환
	 */
	@GetMapping
	public ResponseEntity<FinancialUserProfileInfo> getFinancialUserProfile(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		FinancialUserProfileInfo profile = financialUserProfileService
				.getFinancialUserProfileInfo(userDetails.getUserId());
		return ResponseEntity.ok(profile);
	}

}
