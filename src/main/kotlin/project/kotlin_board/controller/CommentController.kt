package project.kotlin_board.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import project.kotlin_board.dto.request.CommentDeleteRequest
import project.kotlin_board.dto.request.CommentRequest
import project.kotlin_board.dto.response.CommentResponse

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
class CommentController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@PathVariable articleId: Long, @RequestBody commentRequest: CommentRequest): CommentResponse {
        //서비스 로직 호출

        return CommentResponse(1L, "", "")
    }

    @PutMapping("/{commentId}")
    fun update(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @RequestBody commentRequest: CommentRequest
    ): CommentResponse {
        // 서비스 로직 호출

        return CommentResponse(1L, "", "")
    }

    @DeleteMapping("/{commentId}")
    fun delete(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @RequestBody commentDeleteRequest: CommentDeleteRequest
    ) {

        //서비스 로직 호출
    }


}
