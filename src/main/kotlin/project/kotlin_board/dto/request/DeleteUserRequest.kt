package project.kotlin_board.dto.request

import javax.validation.constraints.NotBlank

class DeleteUserRequest(
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String
)

