package project.kotlin_board.dto.request

import javax.validation.constraints.NotBlank

data class ArticleRequest(
    @field:NotBlank(message = "Title cannot be blank or empty")
    val title: String,

    @field:NotBlank(message = "Content cannot be blank or empty")
    val content: String
)
