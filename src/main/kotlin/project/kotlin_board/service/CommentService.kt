package project.kotlin_board.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.CommentRequest
import project.kotlin_board.dto.response.CommentResponse
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.entity.Article
import project.kotlin_board.model.entity.Comment
import project.kotlin_board.model.repository.ArticleRepository
import project.kotlin_board.model.repository.CommentRepository
import project.kotlin_board.model.repository.UserRepository

@Service
@Transactional
class CommentService(
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
) {

    fun create(articleId: Long, request: CommentRequest, userDto: UserDto): CommentResponse {
        val user = userRepository.getReferenceById(userDto.id)

        // 존재하는 게시글 검사
        val article = validateArticleById(articleId)

        val comment = commentRepository.save(
            Comment(content = request.content, user = user, article = article)
        )

        user.addComment(comment)
        article.addComment(comment)

        // 댓글 저장
        return CommentResponse(
            id = comment.id, email = comment.user.email, content = comment.content,
        )
    }

    fun update(articleId: Long, commentId: Long, request: CommentRequest, userDto: UserDto): CommentResponse {
        val user = userRepository.getReferenceById(userDto.id)

        // 존재하는 게시글 검사
        validateArticleById(articleId)

        // 존재하는 댓글 검사
        val comment = validateCommentByIdAndArticleId(commentId, articleId)

        // 작성자와 일치 하는지 검사
        if (user != comment.user) {
            throw BoardApplicationException(ErrorCode.INVALID_PERMISSION)
        }

        comment.updateContent(request.content)

        val savedComment = commentRepository.save(comment)

        return CommentResponse(
            id = savedComment.id, email = savedComment.user.email, content = savedComment.content,
        )
    }

    fun delete(articleId: Long, commentId: Long, userDto: UserDto) {
        val user = userRepository.getReferenceById(userDto.id)

        // 존재하는 게시글 검사
        validateArticleById(articleId)

        // 존재하는 댓글 검사
        val comment = validateCommentByIdAndArticleId(commentId, articleId)

        // 작성자와 일치 하는지 검사
        if (user != comment.user) {
            throw BoardApplicationException(ErrorCode.INVALID_PERMISSION)
        }

        // 삭제
        commentRepository.delete(comment)
    }

    private fun validateArticleById(articleId: Long): Article {
        return articleRepository.findByIdOrNull(articleId)
            ?: throw BoardApplicationException(ErrorCode.ARTICLE_NOT_FOUND)
    }

    private fun validateCommentByIdAndArticleId(commentId: Long, articleId: Long): Comment {
        return commentRepository.findByIdAndArticleId(commentId, articleId)
            ?: throw BoardApplicationException(ErrorCode.COMMENT_NOT_FOUND)
    }


}
