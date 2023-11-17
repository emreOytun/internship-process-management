package com.teamaloha.internshipprocessmanagement.service.security;

import com.teamaloha.internshipprocessmanagement.constants.InternshipProcessManagementConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // It's generated using the website; and this key is secret and unique for my application.
    private final String SECRET_KEY;

    public JwtService(@Value("${SECRET_KEY}") String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    // Generates JWT Token using extra claims map and username subject from our UserDetails class.
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        int totalExpireTimeInMs = 1000 * InternshipProcessManagementConstants.USER_EXPIRE_TIME_IN_SECONDS;

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + totalExpireTimeInMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Generates JWT Token using username subject from our UserDetails class.
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String jwtToken) {
        try {
            return !isTokenExpired(jwtToken) && extractUsername(jwtToken) != null;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return false;
    }

    // Checks if the JWT Token is expired or not.
    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    // Extracts the expiration date from the JWT Token.
    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    // Extracts username from the JWT Token.
    public String extractUsername(String jwtToken) { return extractClaim(jwtToken, Claims::getSubject); }

    // Generic function to extract different claims from JTW Token.
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    // This function extracts all the claims from the JWT Token.
    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // It should check the sign of the JWT Token.
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    // It creates Key from my private SECRET_KEY.
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
