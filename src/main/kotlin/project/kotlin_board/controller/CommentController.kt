package project.kotlin_board.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import project.kotlin_board.dto.request.CommentDeleteRequest
import project.kotlin_board.dto.request.CommentRequest
import project.kotlin_board.dto.response.CommentResponse
import project.kotlin_board.service.CommentService
import javax.validation.Valid

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
class CommentController(
    private val commentService: CommentService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@PathVariable articleId: Long, @Valid @RequestBody commentRequest: CommentRequest): CommentResponse {
        return commentService.create(articleId, commentRequest)
    }

    @PutMapping("/{commentId}")
    fun update(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody commentRequest: CommentRequest
    ): CommentResponse {
        return commentService.update(articleId, commentId, commentRequest)
    }

    @DeleteMapping("/{commentId}")
    fun delete(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody commentDeleteRequest: CommentDeleteRequest
    ) {
        return commentService.delete(articleId, commentId, commentDeleteRequest)
    }

}
