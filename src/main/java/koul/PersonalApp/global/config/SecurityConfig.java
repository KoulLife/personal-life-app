package koul.PersonalApp.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import koul.PersonalApp.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

/**
 * 애플리케이션의 전반적인 보안 정책을 설정하는 클래스
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * 비밀번호 암호화 방식을 설정함 (Password4j의 Argon2 알고리즘 사용)
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		// Password4j 라이브러리를 통해 최신 Argon2 알고리즘 적용
		return new Argon2Password4jPasswordEncoder();
	}

	/**
	 * HTTP 요청에 대한 보안 필터 체인을 정의
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// JWT를 사용하므로 stateless하며, CSRF 보호는 비활성화함
			.csrf(csrf -> csrf.disable())

			// 세션을 생성하지 않고 stateless하게 관리
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// URL 접근 권한 설정
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/**").permitAll() // 로그인, 회원가입 관련 경로는 무조건 허용
				.anyRequest().authenticated()            // 그 외 모든 요청은 인증 필요
			)

			// JWT 인증 필터를 표준 로그인 필터(UsernamePasswordAuthenticationFilter)보다 먼저 실행하도록 설정
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * 수동 로그인 로직에서 사용할 AuthenticationManager 빈 등록
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}