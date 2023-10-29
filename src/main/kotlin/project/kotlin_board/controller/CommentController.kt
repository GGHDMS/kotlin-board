package project.kotlin_board.controller

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.CommentRequest
import project.kotlin_board.dto.response.CommentResponse
import project.kotlin_board.security.annotation.LoginUser
import project.kotlin_board.service.CommentService
import javax.validation.Valid

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
class CommentController(
    private val commentService: CommentService,
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(
        @PathVariable articleId: Long,
        @Valid @RequestBody
        commentRequest: CommentRequest,
        @Parameter(hidden = true) @LoginUser userDto: UserDto,
    ): CommentResponse {
        return commentService.create(articleId, commentRequest, userDto)
    }

    @PutMapping("/{commentId}")
    fun update(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody
        commentRequest: CommentRequest,
        @Parameter(hidden = true) @LoginUser userDto: UserDto,
    ): CommentResponse {
        return commentService.update(articleId, commentId, commentRequest, userDto)
    }

    @DeleteMapping("/{commentId}")
    fun delete(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @Parameter(hidden = true) @LoginUser userDto: UserDto,
    ) {
        return commentService.delete(articleId, commentId, userDto)
    }
}
