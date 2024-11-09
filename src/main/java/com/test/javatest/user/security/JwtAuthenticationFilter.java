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

        // 로그인, 회원가입 요청은 필터를 통과하도록 설정
        if (isAuthorizationPassRequest(path)) {
            filterChain.doFilter(req, res);
            return;
        }

        String token = getJwtTokenFromHeader(req);
        if (token == null) {
            unauthorizedResponse(res, "JWT 토큰이 없습니다.");
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
            unauthorizedResponse(res, "토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            unauthorizedResponse(res, "지원되지 않는 형식의 JWT입니다.");
        } catch (MalformedJwtException e) {
            unauthorizedResponse(res, "JWT의 구조가 손상되었거나 올바르지 않습니다.");
        } catch (SignatureException e) {
            unauthorizedResponse(res, "JWT 서명이 유효하지 않습니다.");
        } catch (IllegalArgumentException e) {
            unauthorizedResponse(res, "입력값이 잘못되었습니다.");
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

    // 로그인 및 회원가입 필터 통과
    private boolean isAuthorizationPassRequest(String path) {
        return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/sign-up");
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
    private void unauthorizedResponse(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String responseBody = "{\"error\": \"" + message + "\"}";
        res.getWriter().write(responseBody);
    }
}
