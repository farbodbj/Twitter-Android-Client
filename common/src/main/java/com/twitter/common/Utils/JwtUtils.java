package com.twitter.common.Utils;


import static com.twitter.common.Utils.SafeCall.safe;

import com.sun.net.httpserver.Headers;


import java.util.Date;

import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


//this class is not meant to be instantiated, therefor the constructor is private
public class JwtUtils {
    public final static String name = "Jwt";
    private final static long REFRESH_TOKEN_DURATION = 30 * 24 * 36000; //one month
    private static final String secret = "d05bacfb7fe9d618ac6c381c1964c52f";

    private JwtUtils(){}

    public static String refreshTokenGenerator(int userId) {
        Date issuedAt = new Date();
        Date exp = new Date(issuedAt.getTime() + REFRESH_TOKEN_DURATION);

        Claims claims = Jwts.claims().setSubject("UserId");
        claims.put("UserId", userId);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256,secret.getBytes())
                .compact();
    }

    public static boolean JwtValidator(String Jwt, Map<String, Object> toCheck) {
        return safe(()-> {

            // Verify the JWT token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(Jwt)
                    .getBody();

            return validateClaims(toCheck, claims) && validateExp(claims);
        }, false);

    }

    private static boolean validateExp(Claims claims) {
        Date expirationDate = claims.getExpiration();
        return expirationDate.after(new Date());
    }

    private static boolean validateClaims(Map<String, Object> toCheck, Claims claims) {
        for (String key: toCheck.keySet()) {
            if(!(claims.containsKey(key) && claims.get(key).equals(claims.get(key))))
                return false;
        }
        return true;
    }

    public static Headers getJwtHeader(int userId) {
        Headers header = new Headers();
        header.add(
                JwtUtils.name,
                JwtUtils.refreshTokenGenerator(userId));
        return header;
    }

    public static Headers getJwtHeader(String userId) {
        Headers header = new Headers();
        header.add(
                JwtUtils.name,
                JwtUtils.refreshTokenGenerator(Integer.parseInt(userId)));
        return header;
    }

}

