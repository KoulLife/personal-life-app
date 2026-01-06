package koul.PersonalApp.user.service;

import koul.PersonalApp.user.dto.LoginRequest;
import koul.PersonalApp.user.dto.SignupRequest;
import koul.PersonalApp.user.dto.TokenResponse;

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
}
