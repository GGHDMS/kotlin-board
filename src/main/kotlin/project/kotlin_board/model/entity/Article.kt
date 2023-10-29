package project.kotlin_board.model.entity

import project.kotlin_board.model.BaseTimeEntity
import javax.persistence.*

@Entity
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    val id: Long = 0L,

    @Column(length = 255)
    var title: String,

    @Column(length = 255)
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @OneToMany(mappedBy = "article", cascade = [CascadeType.REMOVE])
    var comments: MutableList<Comment> = mutableListOf(),

) : BaseTimeEntity() {

    fun addComment(comment: Comment) {
        comments.add(comment)
        comment.article = this
    }

    fun updateTitleAndContent(title: String, content: String) {
        this.title = title
        this.content = content
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Article) return false

        // 객체의 id가 0L이면 항상 false 반환
        if (id == 0L || other.id == 0L) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        // 객체의 id가 0L이면 기본 hashCode 반환
        return if (id == 0L) super.hashCode() else id.hashCode()
    }
}
