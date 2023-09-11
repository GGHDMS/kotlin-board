package project.kotlin_board.model.entity

import project.kotlin_board.model.BaseTimeEntity
import javax.persistence.*

@Entity
@Table(name = "users")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long = 0L,

    @Column(length = 255)
    var email : String,

    @Column(length = 255)
    var username : String,

    @Column(length = 255)
    var password : String
) : BaseTimeEntity()
