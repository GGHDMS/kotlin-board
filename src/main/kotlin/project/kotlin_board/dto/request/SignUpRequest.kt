package project.kotlin_board.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import project.kotlin_board.model.Role
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Schema(description = "회원가입 request")
data class SignUpRequest(
    @field:Email(message = "Please provide a valid email address")
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,

    val username: String,

    val role: Role,
)
