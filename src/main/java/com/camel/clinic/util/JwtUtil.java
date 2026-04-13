package com.camel.clinic.util;

import com.camel.clinic.entity.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${jwt.refresh-cookie-name}")
    private String refreshCookieName;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private PrivateKey getPrivateKey() {
        if (privateKey == null) {
            try {
                // Get project root directory
                Path keyPath = Paths.get(privateKeyPath);

                // If path is relative, resolve it from project root
                if (!keyPath.isAbsolute()) {
                    String projectRoot = new File("").getAbsolutePath();
                    keyPath = Paths.get(projectRoot, privateKeyPath);
                }

                log.debug("Loading private key from: {}", keyPath.toAbsolutePath());

                String key = new String(Files.readAllBytes(keyPath));
                String privateKeyPEM = key
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
                privateKey = keyFactory.generatePrivate(keySpec);

                log.info("Successfully loaded private key");
            } catch (Exception e) {
                log.error("Error loading private key from path: {}", privateKeyPath, e);
                throw new RuntimeException("Failed to load private key. Make sure to run KeyGenerator first.", e);
            }
        }
        return privateKey;
    }

    private PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                // Get project root directory
                Path keyPath = Paths.get(publicKeyPath);

                // If path is relative, resolve it from project root
                if (!keyPath.isAbsolute()) {
                    String projectRoot = new File("").getAbsolutePath();
                    keyPath = Paths.get(projectRoot, publicKeyPath);
                }

                log.debug("Loading public key from: {}", keyPath.toAbsolutePath());

                String key = new String(Files.readAllBytes(keyPath));
                String publicKeyPEM = key
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
                publicKey = keyFactory.generatePublic(keySpec);

                log.info("Successfully loaded public key");
            } catch (Exception e) {
                log.error("Error loading public key from path: {}", publicKeyPath, e);
                throw new RuntimeException("Failed to load public key. Make sure to run KeyGenerator first.", e);
            }
        }
        return publicKey;
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public String getRefreshCookieName() {
        return refreshCookieName;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("fullName", user.getFullName())
                .claim("status", user.getStatus().name())
                .claim("type", "ACCESS")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public String getTokenType(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    public boolean isRefreshToken(String token) {
        try {
            String type = getTokenType(token);
            return "REFRESH".equals(type);
        } catch (Exception e) {
            log.error("Error checking token type", e);
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            String type = getTokenType(token);
            return "ACCESS".equals(type);
        } catch (Exception e) {
            log.error("Error checking token type", e);
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getPublicKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token) && isRefreshToken(token);
    }
}