package project.kotlin_board.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시물 response")
data class ArticleResponse(
    val id: Long,
    val email: String,
    val title: String,
    val content: String,
)
