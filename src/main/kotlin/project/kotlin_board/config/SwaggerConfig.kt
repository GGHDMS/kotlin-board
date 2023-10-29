package project.kotlin_board.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(@Value("\${springdoc.version}") version: String): OpenAPI {
        val info = Info()
            .title("Yourssu Incubating") // 타이틀
            .version(version) // 문서 버전
            .description("Api Documentation") // 문서 설명

        // Security 스키마 설정
        val bearerAuth: SecurityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("Authorization")
            .`in`(SecurityScheme.In.HEADER)
            .name(HttpHeaders.AUTHORIZATION)

        // Security 요청 설정
        val addSecurityItem = SecurityRequirement()
        addSecurityItem.addList("Authorization")

        return OpenAPI() // Security 인증 컴포넌트 설정
            .components(Components().addSecuritySchemes("Authorization", bearerAuth)) // API 마다 Security 인증 컴포넌트 설정
            .addSecurityItem(addSecurityItem)
            .info(info)
    }
}
