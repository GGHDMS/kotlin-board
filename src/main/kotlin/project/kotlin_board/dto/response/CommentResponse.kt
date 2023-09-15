package project.kotlin_board.dto.response

import project.kotlin_board.model.entity.Comment

data class CommentResponse(
    val id: Long,
    val email: String,
    val content: String,
){
    companion object {
        fun fromEntity(entity: Comment): CommentResponse{
            return CommentResponse(
                id = entity.id,
                email = entity.user.email,
                content = entity.content
            )
        }
    }
}
