package project.kotlin_board.dto.request

import javax.validation.constraints.NotBlank

data class CommentRequest(
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,

    @field:NotBlank(message = "Content cannot be blank or empty")
    val content: String
)
