package project.kotlin_board.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import project.kotlin_board.dto.request.ArticleDeleteRequest
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.dto.response.ArticleResponse
import project.kotlin_board.service.ArticleService
import javax.validation.Valid

@RestController
@RequestMapping("/api/articles")
class ArticleController(
    private val articleService: ArticleService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@Valid @RequestBody articleRequest: ArticleRequest): ArticleResponse {
        return articleService.create(articleRequest)
    }

    @PutMapping("/{articleId}")
    fun update(@PathVariable articleId: Long, @Valid @RequestBody articleRequest: ArticleRequest): ArticleResponse {
        return articleService.update(articleId = articleId, request = articleRequest)
    }

    @DeleteMapping("/{articleId}")
    fun delete(@PathVariable articleId: Long, @Valid @RequestBody articleDeleteRequest: ArticleDeleteRequest) {
        return articleService.delete(articleId = articleId, request = articleDeleteRequest)
    }
}
