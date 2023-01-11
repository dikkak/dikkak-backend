package com.dikkak.service;

import com.dikkak.common.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.dikkak.common.ResponseMessage.EXPIRED_TOKEN;
import static com.dikkak.common.ResponseMessage.INVALID_TOKEN;

@Component
public class JwtProvider {

    private static final int ACCESS_TOKEN_MILLISECONDS = 1000 * 60 * 60;               // 1시간
    private static final int REFRESH_TOKEN_MILLISECONDS = 1000 * 60 * 60 * 24 * 14;    // 2주

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private static final Key key = Keys.secretKeyFor(signatureAlgorithm);

    public String createAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_MILLISECONDS))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_MILLISECONDS))
                .signWith(key)
                .compact();
    }

    // access token 검사 및 userId 추출
    public Long validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {   // 토큰이 만료된 경우
            throw new BaseException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new BaseException(INVALID_TOKEN);
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key.getEncoded())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
