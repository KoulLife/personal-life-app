package koul.PersonalApp.user.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 성공 시 클라이언트에게 반환할 JWT 토큰 정보
 */
@Getter
@Builder
public class TokenResponse {

    private String accessToken;
    private String tokenType;

    public static TokenResponse of(String accessToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }
}
