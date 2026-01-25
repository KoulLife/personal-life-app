package koul.PersonalApp.financial.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import koul.PersonalApp.financial.entity.FinancialUserProfile;

/**
 * 재정 유저 프로필 레포지토리
 */
public interface FinancialUserProfileRepository extends JpaRepository<FinancialUserProfile, Long> {

    /**
     * userId로 재정 프로필 조회
     */
    Optional<FinancialUserProfile> findByUser_UserId(Long userId);
}
