package com.dikkak.service;

import com.dikkak.repository.UserRepository;
import com.dikkak.dto.common.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import static com.dikkak.dto.common.ResponseMessage.*;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final int accessTokenMs = 1000 * 60 * 60;               // 1시간
    private final int refreshTokenMs = 1000 * 60 * 60 * 24 * 14;    // 2주

    private final static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private final static Key key = Keys.secretKeyFor(signatureAlgorithm);

    private final UserRepository userRepository;


    public String createAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(now.getTime() + accessTokenMs))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(now.getTime() + refreshTokenMs))
                .signWith(key)
                .compact();
    }

    // access token 검사 및 userId 추출
    public Long validateToken(String token) throws BaseException {
        Claims claims;
        try {
            claims = extractClaims(token);
        } catch (ExpiredJwtException e) {   // 토큰이 만료된 경우
            throw new BaseException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new BaseException(INVALID_TOKEN);
        }
        return Long.parseLong(claims.getSubject());
    }


    private Claims extractClaims(String token) {
        Claims claims;
        claims = Jwts.parserBuilder()
                .setSigningKey(key.getEncoded())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    // 만료된 access token에서 userId 추출
    private Long validateExpiredAccessToken(String token) throws BaseException {
        Claims claims;
        try {
            claims = extractClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            return Long.parseLong(e.getClaims().getSubject());
        } catch (Exception e) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }
    }
}
