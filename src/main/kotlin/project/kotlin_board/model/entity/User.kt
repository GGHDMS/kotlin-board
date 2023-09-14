package project.kotlin_board.model.entity

import project.kotlin_board.model.BaseTimeEntity
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long = 0L,

    @Column(unique = true, length = 255)
    var email: String,

    @Column(length = 255)
    var username: String,

    @Column(length = 255)
    var password: String,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    var articles: MutableList<Article> = mutableListOf()

) : BaseTimeEntity() {

    fun addArticle(article: Article) {
        articles.add(article)
        article.user = this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode() ?: 0
    }
}

