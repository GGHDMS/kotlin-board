package project.kotlin_board.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.kotlin_board.model.entity.Article

interface ArticleRepository : JpaRepository<Article, Long> {
}
