package koul.PersonalApp.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성, 추출 및 검증을 담당하는 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	private final long tokenValidTime = 30 * 60 * 1000L; // 토큰 유효시간 30분
	private final UserDetailsService userDetailsService;
	private SecretKey key;

	/**
	 * Bean 초기화 후 SecretKey 생성
	 */
	@PostConstruct
	protected void init() {
		// 비밀키 문자열을 UTF-8 바이트로 변환하여 HMAC 알고리즘용 키 생성
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 사용자의 고유 식별자(PK)를 기반으로 JWT 토큰 생성
	 */
	public String createToken(String userPk) {
		Date now = new Date();
		return Jwts.builder()
			.subject(userPk) // 클레임에 사용자 PK 저장
			.issuedAt(now) // 발급 시간
			.expiration(new Date(now.getTime() + tokenValidTime)) // 만료 시간
			.signWith(key) // 생성된 키로 서명
			.compact();
	}

	/**
	 * 토큰에서 사용자 정보를 추출하여 Authentication 객체 반환
	 */
	public Authentication getAuthentication(String token) {
		// 토큰 내부의 subject(PK)로 사용자 조회
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(this.getUserPk(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	/**
	 * 토큰의 Payload에서 Subject(사용자 PK) 추출
	 */
	public String getUserPk(String token) {
		return Jwts.parser()
			.verifyWith(key) // 서명 검증 키 설정
			.build()
			.parseSignedClaims(token) // 토큰 파싱
			.getPayload()
			.getSubject();
	}

	/**
	 * HTTP 요청 헤더에서 Bearer 토큰 추출
	 */
	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		// Bearer 접두사 확인 후 실제 토큰만 반환
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	/**
	 * 토큰의 유효성 및 만료 여부 검증
	 */
	public boolean validateToken(String jwtToken) {
		try {
			Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(jwtToken);
			return true;
		} catch (Exception e) {
			// 변조되거나 만료된 토큰일 경우 false 반환
			return false;
		}
	}
}