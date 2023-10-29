package project.kotlin_board.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "댓글 response")
data class CommentResponse(
    val id: Long,
    val email: String,
    val content: String,
)
