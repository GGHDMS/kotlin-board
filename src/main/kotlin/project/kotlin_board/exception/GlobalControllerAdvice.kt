package project.kotlin_board.exception

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
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
            requestURI = request.getDescription(false),
        )
        return ResponseEntity.status(e.errorCode.status).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        e: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error("Validation error occurs {}", e.toString())

        val errorResponse = ErrorResponse(
            time = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST,
            message = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Validation error",
            requestURI = request.getDescription(false),
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MissingKotlinParameterException::class)
    fun handleMissingKotlinParameter(
        e: MissingKotlinParameterException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error("Missing parameter error occurs {}", e.toString())

        val errorResponse = ErrorResponse(
            time = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST,
            message = "Missing parameter: ${e.parameter.name}",
            requestURI = request.getDescription(false),
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(
        e: BindException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error("Bind error occurs {}", e.toString())

        val errorResponse = ErrorResponse(
            time = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST,
            message = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Bind error",
            requestURI = request.getDescription(false),
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(AssertionError::class)
    fun handleAssertionError(
        e: AssertionError,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error("Assertion error occurs {}", e.toString())

        val errorResponse = ErrorResponse(
            time = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST,
            message = e.message ?: "Assertion error",
            requestURI = request.getDescription(false),
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
