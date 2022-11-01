package com.example.onlineshopbot.service;

import com.example.onlineshopbot.enums.RoleName;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SecurityService {
    @Value(value = "${spring.jwt.secret.key}")
    private String secretKey;

    @Value(value = "${spring.jwt.expire.date.minute}")
    private Long expireDateMinute;

    public String generationToken(String subject, Set<RoleName> userRoles) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", userRoles)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(expireDateMinute, ChronoUnit.MINUTES)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Map<String, Object> parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}