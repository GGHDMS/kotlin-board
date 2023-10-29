package project.kotlin_board.model.entity

import project.kotlin_board.model.BaseTimeEntity
import project.kotlin_board.model.Role
import javax.persistence.*

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_email", columnList = "email", unique = true),
    ],
)
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

    @Column(length = 255, nullable = true)
    var refreshToken: String? = null,

    @Column(length = 255)
    @Enumerated(EnumType.STRING)
    var role: Role,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    var articles: MutableList<Article> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    var comments: MutableList<Comment> = mutableListOf(),

) : BaseTimeEntity() {

    fun addArticle(article: Article) {
        articles.add(article)
        article.user = this
    }

    fun addComment(comment: Comment) {
        comments.add(comment)
        comment.user = this
    }

    fun saveRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        // 객체의 id가 0L이면 항상 false 반환
        if (id == 0L || other.id == 0L) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        // 객체의 id가 0L이면 기본 hashCode 반환
        return if (id == 0L) super.hashCode() else id.hashCode()
    }
}
