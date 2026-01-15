package koul.PersonalApp.financial.repository;

import koul.PersonalApp.financial.entity.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.financial.financialId = :financialId")
    Page<FinancialRecord> findByFinancialId(@Param("financialId") Long financialId, Pageable pageable);

    @Query("SELECT DAY(fr.createdAt), SUM(fr.amount) FROM FinancialRecord fr " +
            "WHERE fr.financial.user.userId = :userId " +
            "AND YEAR(fr.createdAt) = :year " +
            "AND MONTH(fr.createdAt) = :month " +
            "GROUP BY DAY(fr.createdAt)")
    java.util.List<Object[]> findDailyExpenseSum(@Param("userId") Long userId, @Param("year") int year,
            @Param("month") int month);
}
