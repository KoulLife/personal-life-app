package koul.PersonalApp.financial.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import koul.PersonalApp.alert.notifier.AlertNotifier;
import koul.PersonalApp.financial.dto.FinancialRecordCommand;
import koul.PersonalApp.financial.dto.FinancialRecordInfo;
import koul.PersonalApp.financial.dto.MonthlyFinancialInfo;
import koul.PersonalApp.financial.entity.Financial;
import koul.PersonalApp.financial.entity.FinancialRecord;
import koul.PersonalApp.financial.repository.FinancialRecordRepository;
import koul.PersonalApp.financial.repository.FinancialRepository;
import koul.PersonalApp.user.entity.ServiceType;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialServiceImpl implements FinancialService {

	private final FinancialRepository financialRepository;
	private final FinancialRecordRepository financialRecordRepository;
	private final UserRepository userRepository;
	private final AlertNotifier alertNotifier;

	// 큰 지출로 판단할 임계값 (100만원)
	private static final Long LARGE_EXPENSE_THRESHOLD = 1_000_000L;

	/**
	 * 금융 대시보드 요약 정보 조회
	 * 현재 월의 총 수익, 가용 자금, 총 지출 정보를 반환
	 */
	@Override
	public MonthlyFinancialInfo getFinancialSummary(Long userId) {
		LocalDate now = LocalDate.now();

		return financialRepository.findByUserIdAndYearMonth(userId, now)
				.map(Financial::getMonthlyFinancialInfo)
				.orElse(null);
	}

	@Override
	@Transactional
	public void registerFinancialStatus(Long userId, MonthlyFinancialInfo info) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

		LocalDate now = LocalDate.now();

		if (financialRepository.findByUserIdAndYearMonth(userId, now).isPresent()) {
			throw new IllegalStateException("이번 달의 재정 상태가 이미 등록되었습니다.");
		}

		Financial financial = Financial.builder()
				.user(user)
				.yearAndMonth(now)
				.revenue(info.monthlyRevenue())
				.availableFunds(info.monthlyAvailableFunds())
				.expenses(info.monthlyExpenses())
				.build();

		financialRepository.save(financial);
	}

	@Override
	@Transactional
	public void updateFinancialStatus(Long userId, MonthlyFinancialInfo info) {
		LocalDate now = LocalDate.now();
		Financial financial = financialRepository.findByUserIdAndYearMonth(userId, now)
				.orElseThrow(() -> new IllegalArgumentException("재정 정보가 존재하지 않습니다."));

		financial.updateRevenue(info.monthlyRevenue());
		financial.updateAvailableFunds(info.monthlyAvailableFunds());
		financial.updateExpenses(info.monthlyExpenses());
	}

	@Override
	@Transactional
	public void addFinanceRecord(Long userId, FinancialRecordCommand command) {
		LocalDate now = LocalDate.now();
		Financial financial = financialRepository.findByUserIdAndYearMonth(userId, now)
				.orElseThrow(() -> new IllegalArgumentException("재정 정보가 존재하지 않습니다."));

		FinancialRecord record = FinancialRecord.builder()
				.financial(financial)
				.description(command.description())
				.amount(command.amount())
				.category(command.category())
				.build();

		financial.getFinancialRecords().add(record);

		// 큰 지출이 발생하면 알림 전송
		if (command.amount() >= LARGE_EXPENSE_THRESHOLD) {
			String message = String.format("큰 지출이 발생했습니다: %s - %,d원", 
					command.description(), command.amount());
			alertNotifier.notifyUser(userId, ServiceType.FINANCIAL_MANAGER, "LARGE_EXPENSE", message);
		}
	}

	@Override
	public Page<FinancialRecordInfo> getRecentFinanceRecord(Long userId, Pageable pageable) {
		LocalDate now = LocalDate.now();

		return financialRepository.findByUserIdAndYearMonth(userId, now)
				.map(financial -> financialRecordRepository.findByFinancialId(financial.getFinancialId(), pageable)
						.map(record -> FinancialRecordInfo.builder()
								.financialRecordId(record.getFinancialRecordId())
								.description(record.getDescription())
								.amount(record.getAmount())
								.category(record.getCategory())
								.createdDate(record.getCreatedAt())
								.build()))
				.orElse(Page.empty());
	}

	@Override
	public Map<Integer, Long> getMonthlyExpenseHeatmap(Long userId, int year, int month) {
		List<Object[]> results = financialRecordRepository.findDailyExpenseSum(userId, year, month);

		return results.stream()
				.collect(Collectors.toMap(
						row -> (Integer) row[0], // 일 (day)
						row -> (Long) row[1] // 일간 지출 합 (sum amount)
				));
	}

	@Override
	public String getAIReport(Long userId) {
		LocalDate now = LocalDate.now();

		return financialRepository.findByUserIdAndYearMonth(userId, now)
				.map(Financial::getAiFeedback)
				.orElse("AI가 리포트를 생성 중입니다...");
	}

	/**
	 * AI 리포트 주기적 작성
	 * 배치 작업에서 호출되어 AI가 생성한 피드백을 저장
	 */
	@Override
	@Transactional
	public void upsertPeriodicAiReport(Long userId, String report) {
		LocalDate now = LocalDate.now();

		Financial financial = financialRepository.findByUserIdAndYearMonth(userId, now)
				.orElseThrow(() -> new IllegalStateException("재정 데이터가 없습니다."));

		financial.updateAiFeedback(report);
		financialRepository.save(financial);
	}

	@Override
	public Map<String, List<Long>> getFinancialGraphData(Long userId) {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusMonths(8); // 9개월 전

		// 최근 9개월의 재정 정보 조회
		List<Financial> financials = financialRepository.findRecentFinancials(userId, startDate.withDayOfMonth(1));

		// 재정 정보를 yyyy-mm 형식의 key로 변환
		Map<String, Financial> financialMap = financials.stream()
				.collect(Collectors.toMap(
						f -> f.getYearAndMonth().getYear() + "-" + f.getYearAndMonth().getMonthValue(),
						f -> f));

		// 결과 리스트 초기화
		List<Long> revenues = new java.util.ArrayList<>();
		List<Long> availableFunds = new java.util.ArrayList<>();
		List<Long> expenses = new java.util.ArrayList<>();

		// 최근 9개월의 데이터를 순회하여 데이터를 채움
		for (int i = 0; i < 9; i++) {
			LocalDate date = startDate.plusMonths(i);
			String key = date.getYear() + "-" + date.getMonthValue();

			Financial financial = financialMap.get(key);

			if (financial != null) {
				revenues.add(financial.getRevenue());
				availableFunds.add(financial.getAvailableFunds());
				expenses.add(financial.getExpenses());
			} else {
				revenues.add(0L);
				availableFunds.add(0L);
				expenses.add(0L);
			}
		}

		return Map.of(
				"revenue", revenues,
				"availableFunds", availableFunds,
				"expenses", expenses);
	}
}
