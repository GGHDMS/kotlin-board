package project.kotlin_board.dto.request

import project.kotlin_board.model.entity.Article
import project.kotlin_board.model.entity.User
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
) {
    fun toEntity(user: User): Article {
        return Article(
            title = title,
            content = content,
            user = user
        )
    }
}
