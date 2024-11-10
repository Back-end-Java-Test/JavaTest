package com.test.javatest.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openApi() {
        final String jwtSecurityName = "bearerAuth"; // 헤더 인증 Key (중요!!)
        return new OpenAPI()
            .components(
                new Components().addSecuritySchemes(jwtSecurityName, securityScheme(jwtSecurityName)) // Header 토큰 인증 정보를 Swagger 문서에 등록
            )
            .info(swaggerInfo());
    }
    // Header 토큰 인증 정보 생성
    private SecurityScheme securityScheme(String jwtSecurityName) {
        return new SecurityScheme()
            .name(jwtSecurityName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("Bearer") // 토큰 Key
            .bearerFormat("JWT"); // 토큰 Value Format
    }

    // 문서 기본 정보
    private Info swaggerInfo() {
        return new Info()
            .title("Basic 4회차 Swagger 프로젝트")
            .description("Spring 심화 과정 Basic 반 4회차 프로젝트 입니다.")
            .version("1.0.0");
    }
}