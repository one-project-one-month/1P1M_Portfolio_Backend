package com._p1m.portfolio.security.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component

public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long TOKEN_VALID_TIME_MILLIS(){
        long validHour = 12;
        return validHour * 60 * 60 * 1000L;
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String extractEmail(String token) throws ExpiredJwtException, JwtException{
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();

        // Adding Custom Claims ( Testing Yet :3)
        String email = claims.get("email" , String.class);
        return (email != null) ? email : claims.getSubject();
    }

    public boolean validateToken(String token , UserDetails userDetails){
        try {
            final String username = extractEmail(token);
            return false;
        }catch (JwtException e) {
            return false;
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }
        return null;
    }

    // Important Code to Check later =_=
    public String generateToken(final String email){
        return generateToken(email , TOKEN_VALID_TIME_MILLIS());
    }

    public String generateToken(final String email ,final long expirationTime){
        return Jwts.builder()
                .setSubject(email)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(final String token){
        try{
           Claims claims = Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJwt(token)
                   .getBody();
           return claims.getExpiration().before(new Date());
        }catch (JwtException e){
            return  true;
        }
    }
}

































