package project.kotlin_board.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank

@Schema(description = "게시물 생성 request")
data class ArticleRequest(
    @field:NotBlank(message = "Title cannot be blank or empty")
    val title: String,

    @field:NotBlank(message = "Content cannot be blank or empty")
    val content: String,
)
