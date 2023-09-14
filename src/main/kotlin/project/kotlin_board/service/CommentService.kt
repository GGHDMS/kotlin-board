package project.kotlin_board.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.request.CommentDeleteRequest
import project.kotlin_board.dto.request.CommentRequest
import project.kotlin_board.dto.response.CommentResponse

@Service
@Transactional
class CommentService {

    fun create(articleId : Long, request: CommentRequest): CommentResponse {
        // 존재하는 회원 검사

        // 이메일 비밀 번호 체크

        // 존재하는 게시글 검사

        // 댓글 저장


        return CommentResponse(1L, "", "")
    }

    fun update(articleId: Long, commentId: Long, request: CommentRequest): CommentResponse {
        // 존재하는 회원 검사

        // 이메일 비밀 번호 체크

        // 존재하는 게시글 검사

        // 존재하는 댓글 검사

        // 작성자와 일치 하는지 검사

        // 수정
        return CommentResponse(1L, "",  "")
    }

    fun delete(articleId: Long, commentId: Long, request: CommentDeleteRequest) {
        // 존재하는 회원 검사

        // 이메일 비밀 번호 체크

        // 존재하는 게시글 검사

        // 존재하는 댓글 검사

        // 작성자와 일치 하는지 검사

        // 삭제
    }

}
