package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kgu.studywithme.global.utils.PasswordEncoderUtils.ENCODER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final MemberFindService memberFindService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenManager tokenManager;

    @Transactional
    public TokenResponse login(String email, String password) {
        Member member = memberFindService.findByEmail(email);
        validatePassword(member.getPasswordValue(), password);
        return issueTokens(member.getId());
    }

    private void validatePassword(String encodedPassword, String comparePassword) {
        if (isNotCorrectPassword(encodedPassword, comparePassword)) {
            throw StudyWithMeException.type(MemberErrorCode.WRONG_PASSWORD);
        }
    }

    private boolean isNotCorrectPassword(String encodedPassword, String comparePassword) {
        return !ENCODER.matches(comparePassword, encodedPassword);
    }

    private TokenResponse issueTokens(Long memberId) {
        String accessToken = jwtTokenProvider.createAccessToken(memberId);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);
        tokenManager.synchronizeRefreshToken(memberId, refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void logout(Long memberId) {
        tokenManager.deleteRefreshTokenByMemberId(memberId);
    }
}
