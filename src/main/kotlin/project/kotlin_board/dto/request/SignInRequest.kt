package project.kotlin_board.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Schema(description = "로그인 request")
data class SignInRequest(
    @field:Email(message = "Please provide a valid email address")
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,
)
