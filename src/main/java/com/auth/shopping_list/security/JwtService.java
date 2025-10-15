package com.auth.shopping_list.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;
    @Value("${security.jwt.expiration-minutes}")
    private long expirationMinutes;

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(key())
                .compact();
    }

    public String extractUsername(String jwt) {
        return Jwts.parser().verifyWith((SecretKey) key()).build()
                .parseSignedClaims(jwt).getPayload().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String jwt) {
        var claims = Jwts.parser().verifyWith((SecretKey) key()).build()
                .parseSignedClaims(jwt).getPayload();
        Object roles = claims.get("roles");
        return roles instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of();
    }

    public boolean isValid(String jwt, UserDetails user) {
        var parser = Jwts.parser().verifyWith((SecretKey) key()).build();
        var claims = parser.parseSignedClaims(jwt).getPayload();
        var expired = claims.getExpiration().before(new Date());
        return user.getUsername().equals(claims.getSubject()) && !expired;
    }

}
