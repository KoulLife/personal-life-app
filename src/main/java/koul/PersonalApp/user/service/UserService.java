package koul.PersonalApp.user.service;

import java.util.Set;

import koul.PersonalApp.user.dto.LoginRequest;
import koul.PersonalApp.user.dto.SignupRequest;
import koul.PersonalApp.user.dto.TokenResponse;
import koul.PersonalApp.user.entity.ServiceType;

/**
 * 사용자 관련 비즈니스 로직을 정의한 인터페이스
 */
public interface UserService {

    /**
     * 회원가입 처리를 수행함
     * 
     * @param request 회원가입 요청 데이터
     * @return 가입된 사용자의 ID
     */
    Long signup(SignupRequest request);

    /**
     * 로그인 처리를 수행하고 JWT 토큰을 발급함
     * 
     * @param request 로그인 요청 데이터
     * @return 발급된 JWT 토큰 정보
     */
    TokenResponse login(LoginRequest request);

    /**
     * 사용자의 활성 서비스 목록에 새로운 서비스를 추가함
     * 
     * @param userId      서비스를 추가할 사용자 ID
     * @param serviceType 추가할 서비스 타입
     */
    void addService(Long userId, ServiceType serviceType);

    /**
     * 사용자의 활성 서비스 목록에서 특정 서비스를 제거함
     * 
     * @param userId      서비스를 제거할 사용자 ID
     * @param serviceType 제거할 서비스 타입
     */
    void removeService(Long userId, ServiceType serviceType);

    /**
     * 사용자의 활성 서비스 목록을 반환함
     * 
     * @param userId 사용자 ID
     * @return 활성화된 서비스 타입 목록 (Set)
     */
    Set<ServiceType> getActiveServices(Long userId);

}
