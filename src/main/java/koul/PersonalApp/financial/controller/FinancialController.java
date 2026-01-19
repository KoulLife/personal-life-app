package koul.PersonalApp.financial.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import koul.PersonalApp.financial.dto.FinancialRecordCommand;
import koul.PersonalApp.financial.dto.FinancialRecordInfo;
import koul.PersonalApp.financial.dto.FinancialRecordRequest;
import koul.PersonalApp.financial.dto.MonthlyFinancialInfo;
import koul.PersonalApp.financial.service.FinancialService;
import koul.PersonalApp.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

/**
 * 재정 관리 컨트롤러
 * 월간 재정 현황, 소비 기록, AI 리포트, 통계 데이터 등을 제공하는 REST API
 */
@RestController
@RequestMapping("/financial")
@RequiredArgsConstructor
public class FinancialController {

	private final FinancialService financialService;

	/**
	 * 금융 대시보드 요약 정보 조회
	 * 현재 월의 총 수익, 가용 자금, 총 지출 정보를 반환
	 */
	@GetMapping("/summary")
	public ResponseEntity<MonthlyFinancialInfo> getFinancialSummary(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		MonthlyFinancialInfo summary = financialService.getFinancialSummary(userDetails.getUserId());
		return ResponseEntity.ok(summary);
	}

	/**
	 * 재정 상태 등록
	 * 이번 달의 초기 재정 상태를 등록 (중복 등록 불가)
	 */
	@PostMapping("/status")
	public ResponseEntity<String> registerFinancialStatus(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody @Valid MonthlyFinancialInfo request) {
		financialService.registerFinancialStatus(userDetails.getUserId(), request);
		return ResponseEntity.ok("재정 상태가 성공적으로 등록되었습니다.");
	}

	/**
	 * 재정 상태 수정
	 * 이번 달의 재정 상태 정보를 업데이트
	 */
	@PatchMapping("/status")
	public ResponseEntity<String> updateFinancialStatus(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody @Valid MonthlyFinancialInfo request) {
		financialService.updateFinancialStatus(userDetails.getUserId(), request);
		return ResponseEntity.ok("재정 상태가 성공적으로 수정되었습니다.");
	}

	/**
	 * 소비 내역 추가
	 * 현재 월의 재정 기록에 새로운 소비 내역을 추가
	 */
	@PostMapping("/record")
	public ResponseEntity<String> addFinanceRecord(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody @Valid FinancialRecordRequest request) {

		FinancialRecordCommand financialRecordCommand = FinancialRecordCommand.builder()
				.description(request.description())
				.amount(request.amount())
				.category(request.category())
				.build();

		financialService.addFinanceRecord(userDetails.getUserId(), financialRecordCommand);
		return ResponseEntity.ok("소비 내역이 성공적으로 추가되었습니다.");
	}

	/**
	 * 최근 소비 내역 조회 (페이징)
	 * 현재 월의 소비 기록을 최신순으로 페이징하여 반환
	 */
	@GetMapping("/records")
	public ResponseEntity<Page<FinancialRecordInfo>> getRecentFinanceRecords(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<FinancialRecordInfo> records = financialService.getRecentFinanceRecord(userDetails.getUserId(), pageable);
		return ResponseEntity.ok(records);
	}

	/**
	 * 월별 지출 히트맵 데이터 조회
	 * 특정 년월의 일자별 지출 합계를 반환 (일 -> 지출 금액)
	 */
	@GetMapping("/heatmap")
	public ResponseEntity<Map<Integer, Long>> getMonthlyExpenseHeatmap(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("year") int year,
			@RequestParam("month") int month) {
		Map<Integer, Long> heatmap = financialService.getMonthlyExpenseHeatmap(userDetails.getUserId(), year, month);
		return ResponseEntity.ok(heatmap);
	}

	/**
	 * AI 리포트 조회
	 * 현재 월의 AI 분석 피드백을 반환
	 */
	@GetMapping("/ai-report")
	public ResponseEntity<String> getAIReport(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		String report = financialService.getAIReport(userDetails.getUserId());
		return ResponseEntity.ok(report);
	}

	/**
	 * 재정 그래프 데이터 조회
	 * 최근 9개월의 수익, 가용 자금, 지출 데이터를 반환 (그래프용)
	 */
	@GetMapping("/graph")
	public ResponseEntity<Map<String, List<Long>>> getFinancialGraphData(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		Map<String, List<Long>> graphData = financialService.getFinancialGraphData(userDetails.getUserId());
		return ResponseEntity.ok(graphData);
	}

}
