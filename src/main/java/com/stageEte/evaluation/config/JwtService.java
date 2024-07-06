package com.stageEte.evaluation.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY ="0xnu93HxqxlLudXQRPM1yqYjUseK+lo/dkoCC/DzDeFQdJG9jG/RqpnFf88c8pNKWuXapM5zufOmxB4Wt5Qph2/8jgTThklW5G3Yjk96YqSdvd5niiXF7+KqN2OAWgpICjMBAx7Gg5aK6UPKyw0LA9K+zI3GxNojAMEIqIGeGOfHcu/1Y9OJcses7wTnkfQVEqQa9A/e6DLfA3Kv6I98rCjPqTuCieTIyLnRi8rBqsaEdMpdHlIUG4nRz5D1GWLzFNZLj2w6l/jtJKwIK+ZckLTCPvpJbITROmjIseON6ZGEiWI/IvpQqGzuMq0/d58SDILxoY4pNQw7TK/l2VfKLNl7nPNOJ0WRkN3AuAHgXIg=\n";
    public String extractUsername(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public  String generateToken(UserDetails userDetails){
        return  generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails

    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public  boolean isTokenvalid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return  extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInkey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

