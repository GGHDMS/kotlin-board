package project.kotlin_board.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.querydsl.UserRepositoryCustom

interface UserRepository : UserRepositoryCustom, JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}
