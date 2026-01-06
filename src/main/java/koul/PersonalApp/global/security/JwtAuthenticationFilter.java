package koul.PersonalApp.global.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 모든 HTTP 요청에서 JWT 토큰의 유효성을 검사하는 인증 필터
 * OncePerRequestFilter를 상속받아 요청당 한 번만 실행됨을 보장함
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 요청을 가로채어 토큰을 검증하고 시큐리티 컨텍스트에 인증 정보를 저장
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// Request Header에서 JWT 토큰 추출
		String token = jwtTokenProvider.resolveToken(request);

		// 토큰이 존재하고 유효성 검증을 통과한 경우
		if(token != null && jwtTokenProvider.validateToken(token)) {
			// 토큰으로부터 인증 객체(Authentication) 생성
			Authentication auth = jwtTokenProvider.getAuthentication(token);
			// SecurityContext에 인증 객체 저장 (인증된 사용자로 등록)
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		// 필터 체인의 다음 단계로 요청 전달
		filterChain.doFilter(request, response);
	}
}