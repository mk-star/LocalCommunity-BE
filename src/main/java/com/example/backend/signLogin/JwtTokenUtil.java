package com.example.backend.signLogin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtTokenUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static Key getKey() {
        return key;
    }

    // JWT Token 발급
    public static String createToken(String userId, Long id, long expireTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);  // 사용자 ID를 클레임에 포함
        System.out.println("에옹2" + id);
        claims.put("id", id);

        // 로그 추가 - 각 사용자에 맞게 userId가 다르게 출력되는지 확인
        System.out.println("Creating JWT Token for userId: " + userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(key)
                .compact();
    }

    // Claims에서 userId 꺼내기
    public static String getuserId(String token) {
        return extractClaims(token).get("userId").toString();
    }

    // Claims에서 id(PK) 꺼내기
    public static Long getId(String token) { return ((Integer) extractClaims(token).get("id")).longValue(); }

    // 발급된 Token이 만료 시간이 지났는지 체크
    public static boolean isExpired(String token) {
        Date expiredDate = extractClaims(token).getExpiration();
        return expiredDate.before(new Date());
    }

    // SecretKey를 사용해 Token Parsing
    private static Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    // 토큰 유효성 검사
    public static boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
