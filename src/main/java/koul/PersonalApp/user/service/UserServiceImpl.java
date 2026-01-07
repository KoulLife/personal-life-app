package koul.PersonalApp.user.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import koul.PersonalApp.global.security.JwtTokenProvider;
import koul.PersonalApp.user.dto.LoginRequest;
import koul.PersonalApp.user.dto.SignupRequest;
import koul.PersonalApp.user.dto.TokenResponse;
import koul.PersonalApp.user.entity.ServiceType;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * UserService 구현체
 * 회원가입 및 로그인에 대한 구체적인 비즈니스 로직을 담당함
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 로직 구현
     * - 중복된 사용자 이름 또는 이메일 검증
     * - 비밀번호 암호화 후 DB 저장
     */
    @Override
    @Transactional
    public Long signup(SignupRequest request) {
        // 유저명 중복 검증
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다: " + request.getUsername());
        }

        // 이메일 중복 검증
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일 계정입니다: " + request.getUsername());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성 및 저장
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .role("USER") // 기본 권한 설정
                .build();

        return userRepository.save(user).getUserId();
    }

    /**
     * 로그인 로직 구현
     * - AuthenticationManager를 통해 인증 수행
     * - 인증 성공 시 JWT 토큰 생성 및 반환
     */
    @Override
    public TokenResponse login(LoginRequest request) {
        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());

        // 실제 검증 (사용자 비밀번호 체크) - 실패 시 예외 발생
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 인증 정보를 기반으로 JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(authentication.getName());

        return TokenResponse.of(accessToken);
    }

    /**
     * 사용자 서비스 추가 구현
     */
    @Override
    @Transactional
    public void addService(Long userId, ServiceType serviceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + userId));
        user.addService(serviceType);
    }

    /**
     * 사용자 서비스 제거 구현
     */
    @Override
    @Transactional
    public void removeService(Long userId, ServiceType serviceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + userId));
        user.getActiveServices().remove(serviceType);
    }
}
