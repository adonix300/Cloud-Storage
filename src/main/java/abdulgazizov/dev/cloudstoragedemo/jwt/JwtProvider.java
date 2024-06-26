package abdulgazizov.dev.cloudstoragedemo.jwt;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    @Autowired
    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.jwtAccessSecret()));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.jwtRefreshSecret()));
        log.debug("Initialized JwtProvider with access secret {} and refresh secret {}", jwtAccessSecret, jwtRefreshSecret);
    }

    public String generateAccessToken(User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant acessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpirationDate = Date.from(acessExpirationInstant);
        log.debug("Generating access token for user {}", user.getUsername());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(accessExpirationDate)
                .signWith(jwtAccessSecret)
                .claim("id", user.getId())
                .claim("roles", user.getRoles())
                .compact();
    }

    public String generateRefreshToken(User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant acessExpirationInstant = now.plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpirationDate = Date.from(acessExpirationInstant);
        log.debug("Generating refresh token for user {}", user.getUsername());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(accessExpirationDate)
                .signWith(jwtAccessSecret)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        log.debug("Validating access token {}", accessToken);
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        log.debug("Validating refresh token {}", refreshToken);
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            log.debug("Token {} is valid", token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    public Claims getAccessClaims(String token) {
        log.debug("Getting access claims for token {}", token);
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(String token) {
        log.debug("Getting refresh claims for token {}", token);
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(String token, Key secret) {
        log.debug("Parsing claims for token {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
