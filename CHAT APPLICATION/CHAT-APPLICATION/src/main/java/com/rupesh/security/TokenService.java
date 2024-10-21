package com.rupesh.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class TokenService {

    private static final String SECRET_KEY = "AB23CD76BD23JH87KL98HJ57BG76TE00LL00TTG86AB23CD76BD23JH87KL98HJ57BG76TE00LL00TTG86AB23CD76BD23JH87KL98HJ57BG76TE00LL00TTG86";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    private TokenService() {
    }

    public static String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("authorities", authorities)
                .signWith(signKey())
                .compact();
    }

    private static Key signKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public static String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private static <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        return claimResolver.apply(
                Jwts
                        .parserBuilder()
                        .setSigningKey(signKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
        );
    }

    public static boolean isTokenExpired(String token) {
        return (extractClaims(token, Claims::getExpiration))
                .before(new Date());
    }

    public static boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token))
                && !isTokenExpired(token);
    }

}