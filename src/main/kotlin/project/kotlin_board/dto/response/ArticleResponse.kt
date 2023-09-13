package project.kotlin_board.dto.response

data class ArticleResponse(
    val id : Long,
    val email : String,
    val title : String,
    val content : String
)
