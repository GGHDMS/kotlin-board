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
    var user : User
) : BaseTimeEntity()


