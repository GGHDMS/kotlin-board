package project.kotlin_board.dto.response

import project.kotlin_board.model.entity.Article

data class ArticleResponse(
    val id: Long,
    val email: String,
    val title: String,
    val content: String
) {
    companion object {
        fun fromEntity(entity: Article): ArticleResponse {
            return ArticleResponse(
                id = entity.id,
                email = entity.user.email,
                title = entity.title,
                content = entity.content
            )
        }
    }
}
