package project.kotlin_board.dto.request

import project.kotlin_board.model.entity.User

data class SignUpRequest(
    val email: String,
    val password: String,
    val username: String
){
    fun toEntity(): User {
        return User(
            email = this.email,
            password = this.password,
            username = this.username
        )
    }
}
