package project.kotlin_board.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.kotlin_board.model.entity.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}
