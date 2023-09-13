package project.kotlin_board.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.request.ArticleDeleteRequest
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.dto.response.ArticleResponse
import project.kotlin_board.model.repository.ArticleRepository

@Service
@Transactional
class ArticleService(
    private val articleRepository: ArticleRepository
) {

    fun create(articleRequest: ArticleRequest): ArticleResponse {
        // 존재하는 회원인지 확인

        // 회원의 패스워드가 맞는지 확인

        // 생성
        return ArticleResponse(1L, "", "", "")
    }

    fun update(articleId : Long, articleRequest: ArticleRequest): ArticleResponse {
        // 존재하는 회원인지 확인

        // 회원의 패스워드가 맞는지 확인

        // 존재하는 게시글인지 확인

        // 게시글의 작성자와 요청한 회원 일치 확인

        // 수정
        return ArticleResponse(1L, "", "", "")
    }

    fun delete(articleId: Long, articleDeleteRequest: ArticleDeleteRequest) {
        // 존재하는 회원인지 확인

        // 회원의 패스워드가 맞는지 확인

        // 존재하는 게시글인지 확인

        // 게시글의 작성자와 요청한 회원 일치 확인

        // 삭제
    }
}
