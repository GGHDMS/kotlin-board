package project.kotlin_board.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.kotlin_board.model.entity.Comment

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByIdAndArticleId(id: Long, articleId: Long): Comment?
}
