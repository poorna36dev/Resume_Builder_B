package com.svu.resume.util;


import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class Jwtutil {
    @Value("${jwt.secret}")
    private  String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpiration; 

    public String generateToken(String Id) {
        Date now=new Date();
        Date expiry=new Date(now.getTime()+jwtExpiration);
        return Jwts.builder().subject(Id).issuedAt(new Date())
                .expiration(expiry)
                .signWith(getSigningKey()).compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
    }

    public String getUserIdFromToken(String token) {
        Claims claims= Jwts.parser()
        .verifyWith(getSigningKey())   // new method
        .build()
        .parseSignedClaims(token)
        .getPayload();
        return claims.getSubject();

    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser()
            .verifyWith(getSigningKey())   // new method
            .build()
            .parseSignedClaims(token)
            .getPayload();
            return true;
        }
        catch(JwtException |IllegalArgumentException e){
            return false;
        }
        
    }
    public boolean isTokenExpired(String token){
        try{
            Claims claims= Jwts.parser()
        .verifyWith(getSigningKey())   // new method
        .build()
        .parseSignedClaims(token)
        .getPayload();
            return claims.getExpiration().before(new Date());
        }
        catch(JwtException |IllegalArgumentException e){
            return false;
        }
    }

    


}