package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.infra.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infra.oauth.dto.response.GoogleUserResponse;
import com.kgu.studywithme.auth.service.dto.response.LoginResponse;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenManager tokenManager;
    private final OAuthConnector oAuthConnector;

    @Transactional
    public LoginResponse login(String code, String redirectUrl) {
        GoogleTokenResponse tokenResponse = (GoogleTokenResponse) oAuthConnector.getToken(code, redirectUrl);
        GoogleUserResponse userInfo = (GoogleUserResponse) oAuthConnector.getUserInfo(tokenResponse.accessToken());

        Long memberId = findMemberOrException(userInfo);
        String accessToken = jwtTokenProvider.createAccessToken(memberId);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        tokenManager.synchronizeRefreshToken(memberId, refreshToken);
        return LoginResponse.builder()
                .userInfo(userInfo)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Long findMemberOrException(GoogleUserResponse userInfo) {
        return memberRepository.findByEmail(Email.from(userInfo.email()))
                .orElseThrow(() -> new StudyWithMeOAuthException(userInfo))
                .getId();
    }

    @Transactional
    public void logout(Long memberId) {
        tokenManager.deleteRefreshTokenByMemberId(memberId);
    }
}
