package koul.PersonalApp.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import koul.PersonalApp.user.dto.LoginRequest;
import koul.PersonalApp.user.dto.SignupRequest;
import koul.PersonalApp.user.dto.TokenResponse;
import koul.PersonalApp.user.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * 인증 관련 API 요청을 처리하는 컨트롤러
 * 회원가입 및 로그인 엔드포인트를 제공함
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 회원가입 요청을 처리함
     * 
     * @param request 회원가입 정보 (username, email, password)
     * @return 가입 성공 시 200 OK
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 로그인 요청을 처리함
     * 
     * @param request 로그인 정보 (username, password)
     * @return 인증 성공 시 JWT 토큰 반환
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = userService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }
}
