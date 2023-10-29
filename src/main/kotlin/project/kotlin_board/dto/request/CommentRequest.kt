package project.kotlin_board.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank

@Schema(description = "댓글 생성 request")
data class CommentRequest(
    @field:NotBlank(message = "Content cannot be blank or empty")
    val content: String,
)
