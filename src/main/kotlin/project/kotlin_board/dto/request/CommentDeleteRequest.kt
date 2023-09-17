package project.kotlin_board.dto.request

import javax.validation.constraints.NotBlank

data class CommentDeleteRequest(
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,
)
