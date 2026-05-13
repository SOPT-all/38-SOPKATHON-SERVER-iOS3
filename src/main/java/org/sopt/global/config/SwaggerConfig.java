package org.sopt.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SOPKATHON iOS3 API")
                        .version("v1")
                        .description("SOPT 38기 솝카톤 iOS3 서버 API 문서"))
                .servers(List.of(new Server().url("/").description("Current Server")));
    }
}
