package project.kotlin_board.dto.request

import project.kotlin_board.model.entity.Article
import project.kotlin_board.model.entity.Comment
import project.kotlin_board.model.entity.User
import javax.validation.constraints.NotBlank

data class CommentRequest(
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,

    @field:NotBlank(message = "Content cannot be blank or empty")
    val content: String
){

    fun toEntity(user: User, article: Article): Comment{
        return Comment(
            content = content,
            user = user,
            article = article
        )
    }
}
