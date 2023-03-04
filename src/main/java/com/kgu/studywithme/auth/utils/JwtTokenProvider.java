package com.kgu.studywithme.auth.utils;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(@Value("${jwt.secret-key}") final String secretKey,
                            @Value("${jwt.access-token-validity}") final long accessTokenValidityInMilliseconds,
                            @Value("${jwt.refresh-token-validity}") final long refreshTokenValidityInMilliseconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    public String createAccessToken(Long memberId) {
        return createToken(memberId, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken(Long memberId) {
        return createToken(memberId, refreshTokenValidityInMilliseconds);
    }

    private String createToken(Long memberId, long validityInMilliseconds) {
        // Payload
        Claims claims = Jwts.claims();
        claims.put("id", memberId);

        // Expires At
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getId(String token) {
        return getClaims(token)
                .getBody()
                .get("id", Long.class);
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }

    public boolean isTokenValid(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            Date expiredDate = claims.getBody().getExpiration();
            Date now = new Date();
            return expiredDate.after(now);
        } catch (ExpiredJwtException e) {
            throw StudyWithMeException.type(AuthErrorCode.AUTH_EXPIRED_TOKEN);
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw StudyWithMeException.type(AuthErrorCode.AUTH_INVALID_TOKEN);
        }
    }
}
