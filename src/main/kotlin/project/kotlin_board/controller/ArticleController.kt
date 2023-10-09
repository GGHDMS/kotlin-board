package project.kotlin_board.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.dto.response.ArticleResponse
import project.kotlin_board.security.annotation.LoginUser
import project.kotlin_board.service.ArticleService
import javax.validation.Valid

@RestController
@RequestMapping("/api/articles")
class ArticleController(
    private val articleService: ArticleService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@Valid @RequestBody articleRequest: ArticleRequest, @LoginUser userDto: UserDto): ArticleResponse {
        return articleService.create(articleRequest, userDto)
    }

    @PutMapping("/{articleId}")
    fun update(
        @PathVariable articleId: Long,
        @Valid @RequestBody articleRequest: ArticleRequest,
        @LoginUser userDto: UserDto
    ): ArticleResponse {
        return articleService.update(articleId, articleRequest, userDto)
    }

    @DeleteMapping("/{articleId}")
    fun delete(@PathVariable articleId: Long, @LoginUser userDto: UserDto) {
        return articleService.delete(articleId, userDto)
    }
}
