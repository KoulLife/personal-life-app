package koul.PersonalApp.financial.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import koul.PersonalApp.financial.dto.FinancialUserProfileCommand;
import koul.PersonalApp.financial.dto.FinancialUserProfileInfo;
import koul.PersonalApp.financial.entity.FinancialUserProfile;
import koul.PersonalApp.financial.repository.FinancialUserProfileRepository;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FinancialUserProfileServiceImplTest {

    @InjectMocks
    private FinancialUserProfileServiceImpl financialUserProfileService;

    @Mock
    private FinancialUserProfileRepository financialUserProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("재정 프로필 설정 - 신규 생성 성공")
    void setFinancialUserProfile_CreateNew_Success() {
        // given
        Long userId = 1L;
        String profileDataJson = "{\"age\":30,\"job\":\"개발자\",\"householdComposition\":\"1인 가구\"}";
        FinancialUserProfileCommand command = FinancialUserProfileCommand.builder()
                .profileData(profileDataJson)
                .build();

        User user = User.builder().build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(financialUserProfileRepository.findByUser_UserId(userId)).willReturn(Optional.empty());

        // when
        financialUserProfileService.setFinancialUserProfile(userId, command);

        // then
        // 새로운 프로필이 생성되어 저장되어야 함
        verify(financialUserProfileRepository, times(1)).save(any(FinancialUserProfile.class));
    }

    @Test
    @DisplayName("재정 프로필 설정 - 기존 프로필 업데이트 성공")
    void setFinancialUserProfile_UpdateExisting_Success() {
        // given
        Long userId = 1L;
        String newProfileData = "{\"age\":31,\"job\":\"시니어 개발자\"}";
        FinancialUserProfileCommand command = FinancialUserProfileCommand.builder()
                .profileData(newProfileData)
                .build();

        User user = User.builder().build();
        FinancialUserProfile existingProfile = FinancialUserProfile.builder()
                .user(user)
                .profileData("{\"age\":30,\"job\":\"개발자\"}")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(financialUserProfileRepository.findByUser_UserId(userId)).willReturn(Optional.of(existingProfile));

        // when
        financialUserProfileService.setFinancialUserProfile(userId, command);

        // then
        // save는 호출되지 않아야 함 (업데이트만 수행)
        verify(financialUserProfileRepository, never()).save(any());
        // 프로필 데이터가 업데이트되었는지 확인
        assertThat(existingProfile.getProfileData()).isEqualTo(newProfileData);
    }

    @Test
    @DisplayName("재정 프로필 설정 - 존재하지 않는 유저로 예외 발생")
    void setFinancialUserProfile_UserNotFound_ThrowsException() {
        // given
        Long userId = 999L;
        FinancialUserProfileCommand command = FinancialUserProfileCommand.builder()
                .profileData("{\"age\":30}")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> financialUserProfileService.setFinancialUserProfile(userId, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원 정보가 없습니다.");

        // 레포지토리 save가 호출되지 않았는지 확인
        verify(financialUserProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("재정 프로필 조회 - 성공")
    void getFinancialUserProfileInfo_Success() {
        // given
        Long userId = 1L;
        String profileData = "{\"age\":30,\"job\":\"개발자\",\"householdComposition\":\"1인 가구\"}";

        User user = User.builder().build();
        FinancialUserProfile profile = FinancialUserProfile.builder()
                .user(user)
                .profileData(profileData)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(financialUserProfileRepository.findByUser_UserId(userId)).willReturn(Optional.of(profile));

        // when
        FinancialUserProfileInfo result = financialUserProfileService.getFinancialUserProfileInfo(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.profileData()).isEqualTo(profileData);
    }

    @Test
    @DisplayName("재정 프로필 조회 - 프로필이 없으면 null 반환")
    void getFinancialUserProfileInfo_NotFound_ReturnsNull() {
        // given
        Long userId = 1L;
        User user = User.builder().build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(financialUserProfileRepository.findByUser_UserId(userId)).willReturn(Optional.empty());

        // when
        FinancialUserProfileInfo result = financialUserProfileService.getFinancialUserProfileInfo(userId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("재정 프로필 조회 - 존재하지 않는 유저로 예외 발생")
    void getFinancialUserProfileInfo_UserNotFound_ThrowsException() {
        // given
        Long userId = 999L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> financialUserProfileService.getFinancialUserProfileInfo(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원 정보가 없습니다.");
    }
}