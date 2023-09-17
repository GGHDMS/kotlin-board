package project.kotlin_board.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class SignUpRequest(
    @field:Email(message = "Please provide a valid email address")
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,

    val username: String
)
