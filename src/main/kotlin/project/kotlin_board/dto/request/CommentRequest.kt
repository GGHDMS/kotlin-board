package project.kotlin_board.dto.request

import javax.validation.constraints.NotBlank

data class CommentRequest(
    @field:NotBlank(message = "Content cannot be blank or empty")
    val content: String
)
