package koul.PersonalApp.financial.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import koul.PersonalApp.financial.dto.FinancialRecordCommand;
import koul.PersonalApp.financial.dto.FinancialRecordInfo;
import koul.PersonalApp.financial.dto.MonthlyFinancialInfo;
import koul.PersonalApp.financial.entity.Financial;
import koul.PersonalApp.financial.entity.FinancialRecord;
import koul.PersonalApp.financial.repository.FinancialRecordRepository;
import koul.PersonalApp.financial.repository.FinancialRepository;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FinancialServiceImplTest {

	@InjectMocks
	private FinancialServiceImpl financialService;

	@Mock
	private FinancialRepository financialRepository;

	@Mock
	private FinancialRecordRepository financialRecordRepository;

	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName("금융 대시보드 요약 정보 조회 - 데이터가 존재할 때 정상 반환해야 함")
	void getFinancialSummary_Success() {
		// given
		Long userId = 1L;
		LocalDate now = LocalDate.now();

		Financial financial = Financial.builder()
				.revenue(5000000L)
				.availableFunds(3000000L)
				.expenses(2000000L)
				.yearAndMonth(now)
				.build();

		given(financialRepository.findByUserIdAndYearMonth(eq(userId), any(LocalDate.class)))
				.willReturn(Optional.of(financial));

		// when
		MonthlyFinancialInfo result = financialService.getFinancialSummary(userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.monthlyRevenue()).isEqualTo(5000000L);
		assertThat(result.monthlyAvailableFunds()).isEqualTo(3000000L);
		assertThat(result.monthlyExpenses()).isEqualTo(2000000L);
	}

	@Test
	@DisplayName("금융 대시보드 요약 정보 조회 - 데이터가 없으면 null 반환")
	void getFinancialSummary_NotFound_ReturnsNull() {
		// given
		Long userId = 1L;
		given(financialRepository.findByUserIdAndYearMonth(eq(userId), any(LocalDate.class)))
				.willReturn(Optional.empty());

		// when
		MonthlyFinancialInfo result = financialService.getFinancialSummary(userId);

		// then
		assertThat(result).isNull();
	}

	@Test
	@DisplayName("재정 상태 등록 - 성공 시 레포지토리 save 호출")
	void registerFinancialStatus_Success() {
		// given
		Long userId = 1L;
		MonthlyFinancialInfo info = new MonthlyFinancialInfo(5000000L, 3000000L, 2000000L);
		User user = User.builder().build(); // 테스트용 유저 객체

		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(financialRepository.findByUserIdAndYearMonth(eq(userId), any(LocalDate.class)))
				.willReturn(Optional.empty()); // 아직 등록 안 된 상태여야 함

		// when
		financialService.registerFinancialStatus(userId, info);

		// then
		verify(financialRepository, times(1)).save(any(Financial.class));
	}

	@Test
	@DisplayName("재정 상태 등록 - 이번 달 데이터가 이미 있으면 예외 발생")
	void registerFinancialStatus_AlreadyExists_ThrowsException() {
		// given
		Long userId = 1L;
		MonthlyFinancialInfo info = new MonthlyFinancialInfo(5000000L, 3000000L, 2000000L);
		User user = User.builder().build();

		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(financialRepository.findByUserIdAndYearMonth(eq(userId), any(LocalDate.class)))
				.willReturn(Optional.of(Financial.builder().build())); // 이미 존재

		// when & then
		assertThatThrownBy(() -> financialService.registerFinancialStatus(userId, info))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("이번 달의 재정 상태가 이미 등록되었습니다.");
	}

	@Test
	@DisplayName("재정 상태 수정 - 성공 시 값 업데이트 확인")
	void updateFinancialStatus_Success() {
		// given
		Long userId = 1L;
		MonthlyFinancialInfo info = new MonthlyFinancialInfo(6000000L, 4000000L, 2000000L);

		// Mock 객체 생성 시점에 값을 설정
		Financial financial = Financial.builder()
				.revenue(5000000L)
				.availableFunds(3000000L)
				.expenses(2000000L)
				.build();

		given(financialRepository.findByUserIdAndYearMonth(eq(userId), any(LocalDate.class)))
				.willReturn(Optional.of(financial));

		// when
		financialService.updateFinancialStatus(userId, info);

		// then
		assertThat(financial.getRevenue()).isEqualTo(6000000L);
		assertThat(financial.getAvailableFunds()).isEqualTo(4000000L);
		// expenses는 로직상 들어온 값으로 덮어씀
		assertThat(financial.getExpenses()).isEqualTo(2000000L);
	}

	@Test
	@DisplayName("소비 내역 추가 - 리스트에 정상적으로 추가되는지 확인")
	void addFinanceRecord_Success() {
		// given
		Long userId = 1L;
		FinancialRecordCommand command = new FinancialRecordCommand("맥북 프로", 3500000L, "전자기기", null);

		Financial financial = Financial.builder()
				.expenses(0L) // 초기 지출 0
				.financialRecords(new ArrayList<>()) // Fix NPE: 명시적 초기화
				.build();

		// 리스트 초기화 (빌더에서 null일 수 있으니)
		financial.getFinancialRecords().clear();

		given(financialRepository.findByUserIdAndYearMonth(eq(userId), any(LocalDate.class)))
				.willReturn(Optional.of(financial));

		// when
		financialService.addFinanceRecord(userId, command);

		// then
		// FinancialRecord 생성 시 생성자 로직에 의해 financial.spend()가 호출되어 expenses가 증가해야 함
		// 다만 FinancialRecord 객체 생성을 서비스 내부에서 하므로, 리스트에 추가되었는지만 우선 검증
		assertThat(financial.getFinancialRecords()).hasSize(1);
		assertThat(financial.getFinancialRecords().get(0).getDescription()).isEqualTo("맥북 프로");
	}

	@Test
	@DisplayName("최근 소비 내역 조회 - 페이징 처리된 DTO 리스트 반환")
	void getRecentFinanceRecord_Success() {
		// given
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 5);
		Financial financial = Financial.builder()
				.expenses(0L) // Fix NPE: spend() 호출을 위해 초기화
				.financialRecords(new ArrayList<>()) // Fix: Good practice
				.build();

		// 1. 현재 월의 Financial 찾기
		given(financialRepository.findByUserIdAndYearMonth(eq(userId), any(LocalDate.class)))
				.willReturn(Optional.of(financial));

		// 2. Financial ID로 레코드 페이징 조회
		List<FinancialRecord> records = List.of(
				FinancialRecord.builder().description("커피").amount(5000L).category("식비").financial(financial).build(),
				FinancialRecord.builder().description("점심").amount(10000L).category("식비").financial(financial).build()
		);
		Page<FinancialRecord> recordPage = new PageImpl<>(records);

		// financial.getFinancialId() 호출 시 null이면 안 되니까 설정 필요, 혹은 lenient()
		// 여기서는 로직 흐름 상 financial 객체가 넘어가므로, repository mock에서 any() 쓰거나 id 설정
		// Financial 엔티티 필드에 Reflection 등으로 ID 부여하거나, mock 객체 행동 정의가 까다로우면 any() 활용
		given(financialRecordRepository.findByFinancialId(any(), eq(pageable)))
				.willReturn(recordPage);

		// when
		Page<FinancialRecordInfo> result = financialService.getRecentFinanceRecord(userId, pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).description()).isEqualTo("커피"); // 순서는 PageImpl 순서 따름
	}

	@Test
	@DisplayName("월별 지출 히트맵 - Aggregation 결과가 Map으로 잘 변환되는지")
	void getMonthlyExpenseHeatmap_Success() {
		// given
		Long userId = 1L;
		int year = 2024;
		int month = 1;

		// Repository에서 Object[] 리스트 반환 (일, 금액)
		List<Object[]> queryResult = new ArrayList<>();
		queryResult.add(new Object[]{1, 50000L});  // 1일: 5만 원
		queryResult.add(new Object[]{15, 120000L}); // 15일: 12만 원

		given(financialRecordRepository.findDailyExpenseSum(userId, year, month))
				.willReturn(queryResult);

		// when
		Map<Integer, Long> resultMap = financialService.getMonthlyExpenseHeatmap(userId, year, month);

		// then
		assertThat(resultMap).hasSize(2);
		assertThat(resultMap.get(1)).isEqualTo(50000L);
		assertThat(resultMap.get(15)).isEqualTo(120000L);
	}

	@Test
	@DisplayName("AI 리포트 조회 - 생성 중일 때 기본 메시지 반환")
	void getAIReport_GeneratingMessage() {
		// given
		Long userId = 1L;
		Financial financial = Financial.builder().build(); // aiFeedback 필드 null 상태

		given(financialRepository.findByUserIdAndYearMonth(eq(userId), any(LocalDate.class)))
				.willReturn(Optional.of(financial));

		// when
		String report = financialService.getAIReport(userId);

		// then
		assertThat(report).isEqualTo("AI가 리포트를 생성 중입니다...");
	}

	@Test
	@DisplayName("재정 그래프 데이터 - 9개월 치 데이터가 0으로 채워져서라도 나와야 함")
	void getFinancialGraphData_FillsZeroForMissingMonths() {
		// given
		Long userId = 1L;
		LocalDate now = LocalDate.now();
		// 일부러 데이터를 하나도 리턴 안 함 (빈 리스트)
		given(financialRepository.findRecentFinancials(eq(userId), any(LocalDate.class)))
				.willReturn(List.of());

		// when
		Map<String, List<Long>> result = financialService.getFinancialGraphData(userId);

		// then
		// 9개월 치 데이터가 모두 0으로 채워져 있어야 함
		assertThat(result.get("revenue")).hasSize(9);
		assertThat(result.get("availableFunds")).hasSize(9);
		assertThat(result.get("expenses")).hasSize(9);

		assertThat(result.get("expenses").get(0)).isEqualTo(0L); // 첫 달도 0
		assertThat(result.get("expenses").get(8)).isEqualTo(0L); // 마지막 달도 0
	}
}