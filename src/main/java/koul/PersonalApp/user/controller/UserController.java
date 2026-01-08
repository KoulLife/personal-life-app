package koul.PersonalApp.user.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import koul.PersonalApp.global.security.CustomUserDetails;
import koul.PersonalApp.user.entity.ServiceType;
import koul.PersonalApp.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * 사용자의 활성화된 서비스 정보 반환
	 */
	@GetMapping("/services")
	public ResponseEntity<Set<ServiceType>> getActiveServices(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		Set<ServiceType> activeServices = userService.getActiveServices(userId);

		return ResponseEntity.ok(activeServices);
	}

	/**
	 * 서비스 신청
	 */
	@PostMapping("/services")
	public ResponseEntity<String> applyService(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("serviceType") ServiceType serviceType) {
		userService.addService(userDetails.getUserId(), serviceType);
		return ResponseEntity.ok("서비스가 성공적으로 신청되었습니다.");
	}

	/**
	 * 서비스 해제
	 */
	@DeleteMapping("/services")
	public ResponseEntity<String> cancelService(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("serviceType") ServiceType serviceType) {
		userService.removeService(userDetails.getUserId(), serviceType);
		return ResponseEntity.ok("서비스가 성공적으로 해제되었습니다.");
	}
}
