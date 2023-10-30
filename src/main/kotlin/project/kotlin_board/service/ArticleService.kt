package project.kotlin_board.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.dto.response.ArticleResponse
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.entity.Article
import project.kotlin_board.model.repository.ArticleRepository
import project.kotlin_board.model.repository.UserRepository

@Service
@Transactional
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
) {

    fun create(request: ArticleRequest, userDto: UserDto): ArticleResponse {
        val user = userRepository.getReferenceById(userDto.id)

        // 생성
        val article = articleRepository.save(
            Article(title = request.title, content = request.content, user = user),
        )

        user.addArticle(article)

        return ArticleResponse(
            id = article.id,
            email = article.user.email,
            title = article.title,
            content = article.content,
        )
    }

    fun update(articleId: Long, request: ArticleRequest, userDto: UserDto): ArticleResponse {
        val user = userRepository.getReferenceById(userDto.id)
        val article = validateArticleById(articleId)

        if (user != article.user) {
            throw BoardApplicationException(ErrorCode.INVALID_PERMISSION)
        }

        article.updateTitleAndContent(title = request.title, content = request.content)

        val savedArticle = articleRepository.save(article)

        return ArticleResponse(
            id = savedArticle.id,
            email = savedArticle.user.email,
            title = savedArticle.title,
            content = savedArticle.content,
        )
    }

    fun delete(articleId: Long, userDto: UserDto) {
        val user = userRepository.getReferenceById(userDto.id)
        val article = validateArticleById(articleId)

        if (user != article.user) {
            throw BoardApplicationException(ErrorCode.INVALID_PERMISSION)
        }

        articleRepository.delete(article)
    }

    private fun validateArticleById(articleId: Long): Article {
        return articleRepository.findByIdOrNull(articleId)
            ?: throw BoardApplicationException(ErrorCode.ARTICLE_NOT_FOUND)
    }
}
