package project.kotlin_board.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import project.kotlin_board.exception.ErrorResponse
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AccessDeniedException,
    ) {
        val errorResponse = ErrorResponse(
            time = LocalDateTime.now(),
            status = HttpStatus.FORBIDDEN,
            message = ex.message!!,
            requestURI = request.requestURI.toString(),
        )

        with(response) {
            status = HttpStatus.FORBIDDEN.value()
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = "UTF-8"
            writer.println(convertJson(errorResponse))
        }
    }

    private fun convertJson(errorResponse: ErrorResponse): String? {
        return ObjectMapper().registerKotlinModule()
            .registerModule(JavaTimeModule())
            .writeValueAsString(errorResponse)
    }
}
