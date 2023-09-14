package project.kotlin_board.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.request.ArticleDeleteRequest
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.dto.response.ArticleResponse
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.entity.Article
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.ArticleRepository
import project.kotlin_board.model.repository.UserRepository

@Service
@Transactional
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder
) {

    fun create(request: ArticleRequest): ArticleResponse {
        val user = validateUserByEmail(request.email)
        validateUserPassword(request.password, user.password)

        // 생성
        val article = request.toEntity(user)
        user.addArticle(article)

        return ArticleResponse.fromEntity(articleRepository.save(article))
    }

    fun update(articleId: Long, request: ArticleRequest): ArticleResponse {
        val user = validateUserByEmail(request.email)
        validateUserPassword(request.password, user.password)

        val article = validateArticleById(articleId)

        if (user != article.user) {
            throw BoardApplicationException(ErrorCode.INVALID_PERMISSION)
        }

        article.updateTitleAndContent(title = request.title, content = request.content)

        return ArticleResponse.fromEntity(articleRepository.save(article))
    }

    fun delete(articleId: Long, request: ArticleDeleteRequest) {
        val user = validateUserByEmail(request.email)
        validateUserPassword(request.password, user.password)

        val article = validateArticleById(articleId)

        if (user != article.user) {
            throw BoardApplicationException(ErrorCode.INVALID_PERMISSION)
        }

        articleRepository.delete(article)
    }

    private fun validateUserByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw BoardApplicationException(ErrorCode.EMAIL_NOT_FOUND)
    }

    private fun validateUserPassword(requestPassword: String, storedPassword: String) {
        if (!encoder.matches(requestPassword, storedPassword)) {
            throw BoardApplicationException(ErrorCode.INVALID_PASSWORD)
        }
    }

    private fun validateArticleById(articleId: Long): Article {
        return articleRepository.findByIdOrNull(articleId)
            ?: throw BoardApplicationException(ErrorCode.ARTICLE_NOT_FOUND)
    }
}
