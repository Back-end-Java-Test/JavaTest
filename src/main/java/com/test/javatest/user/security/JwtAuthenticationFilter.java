package com.test.javatest.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    protected void doFilterInternal(
        HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String path = req.getRequestURI();

        // 로그인, 회원가입 요청 및 Swagger UI 관련 요청은 필터를 통과하도록 설정
        if (isAuthorizationPassRequest(path)) {
            filterChain.doFilter(req, res);
            return;
        }

        String token = getJwtTokenFromHeader(req);
        if (token == null) {
            unauthorizedResponse(res);
            return;
        }

        try {
            SecretKey key = getSecretKey();
            Claims claims = getUserInfoFromToken(token, key);

            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            // UsernamePasswordAuthenticationToken을 사용하여 인증 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.createAuthorityList(role));

            // SecurityContext에 인증 객체 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 후속 필터로 요청 전달
            filterChain.doFilter(req, res);

        } catch (ExpiredJwtException e) {
            unauthorizedResponse(res);
        } catch (UnsupportedJwtException e) {
            unauthorizedResponse(res);
        } catch (MalformedJwtException e) {
            unauthorizedResponse(res);
        } catch (SignatureException e) {
            unauthorizedResponse(res);
        } catch (IllegalArgumentException e) {
            unauthorizedResponse(res);
        }
    }

    // 헤더에서 토큰 추출
    private String getJwtTokenFromHeader(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // 로그인, 회원가입 요청 및 Swagger UI 관련 경로를 필터에서 통과
    private boolean isAuthorizationPassRequest(String path) {
        // Swagger UI 경로를 추가하여 인증 우회
        return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/sign-up")
            || path.startsWith("/swagger-ui/") // Swagger UI 경로 예외 추가
            || path.startsWith("/v3/api-docs/"); // Swagger API Docs 경로 예외 추가
    }

    // 시크릿키 생성
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    // JWT 토큰에서 사용자 정보 추출 및 검증
    public Claims getUserInfoFromToken(String token, SecretKey key)
        throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // JWT 검증 실패 시 응답 처리
    private void unauthorizedResponse(HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String responseBody = "{\"error\": \" 인증에 실패하였습니다. \"}";
        res.getWriter().write(responseBody);
    }
}
