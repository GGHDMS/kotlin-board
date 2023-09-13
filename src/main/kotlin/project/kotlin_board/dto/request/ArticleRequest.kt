package project.kotlin_board.dto.request

import javax.validation.constraints.NotBlank

data class ArticleRequest(

    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,

    @field:NotBlank(message = "Title cannot be blank or empty")
    val title: String,

    @field:NotBlank(message = "Content cannot be blank or empty")
    val content: String
)
