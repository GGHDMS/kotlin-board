package project.kotlin_board.controller

import org.springframework.web.bind.annotation.*
import project.kotlin_board.dto.request.ArticleDeleteRequest
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.dto.response.ArticleResponse

@RestController
@RequestMapping("/articles")
class ArticleController {

    @PostMapping
    fun create(articleRequest: ArticleRequest): ArticleResponse {


        return ArticleResponse(1L, "", "", "")
    }

    @PutMapping("/{articleId}")
    fun update(@PathVariable articleId: Long, articleRequest: ArticleRequest): ArticleResponse {

        return ArticleResponse(1L, "", "", "")
    }

    @DeleteMapping("/{articleId}")
    fun delete(@PathVariable articleId: Long, articleDeleteRequest: ArticleDeleteRequest) {

    }
}
