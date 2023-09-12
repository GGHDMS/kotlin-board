package project.kotlin_board.exception

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@ControllerAdvice
class GlobalControllerAdvice {

    private val log = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)

    @ExceptionHandler(BoardApplicationException::class)
    fun applicationHandler(e: BoardApplicationException, request: WebRequest): ResponseEntity<ErrorResponse> {
        log.error("Error occurs {}", e.toString())

        val errorResponse = ErrorResponse(
            time = LocalDateTime.now(),
            status = e.errorCode.status,
            message = e.message ?: "Unexpected error",
            requestURI = request.getDescription(false)
        )
        return ResponseEntity.status(e.errorCode.status).body(errorResponse)
    }
}

