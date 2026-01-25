package koul.PersonalApp.financial.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import koul.PersonalApp.global.entity.BaseTimeEntity;
import koul.PersonalApp.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinancialUserProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long financialUserProfileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String profileData; // 재정 상태 설정 정보 (JSON 형태)

    @Builder
    public FinancialUserProfile(User user, String profileData) {
        this.user = user;
        this.profileData = profileData;
    }

    public void updateProfileData(String profileData) {
        this.profileData = profileData;
    }
}
