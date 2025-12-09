package com.abahstudio.app.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.access.expiration}")
    private long accessExp;

    @Value("${jwt.refresh.expiration}")
    private long refreshExp;

    // ==========================
    //   PUBLIC API
    // ==========================

    public String generateAccessToken(String username) {
        return createToken(username, accessExp, getAccessKey());
    }

    public String generateRefreshToken(String username) {
        return createToken(username, refreshExp, getRefreshKey());
    }

    public boolean isAccessTokenValid(String token) {
        return !isExpired(token, getAccessKey());
    }

    public boolean isRefreshTokenValid(String token) {
        return !isExpired(token, getRefreshKey());
    }

    public String extractUsernameFromAccess(String token) {
        return extractClaim(token, Claims::getSubject, getAccessKey());
    }

    public String extractUsernameFromRefresh(String token) {
        return extractClaim(token, Claims::getSubject, getRefreshKey());
    }

    // ==========================
    //   INTERNAL METHODS
    // ==========================

    private String createToken(String subject, long expMs, Key key) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isExpired(String token, Key key) {
        return extractExpiration(token, key).before(new Date());
    }

    private Date extractExpiration(String token, Key key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver, Key key) {
        Claims claims = extractAllClaims(token, key);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ==========================
    //   KEY HELPERS
    // ==========================

    private Key getAccessKey() {
        return Keys.hmacShaKeyFor(accessSecret.getBytes());
    }

    private Key getRefreshKey() {
        return Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }
}
