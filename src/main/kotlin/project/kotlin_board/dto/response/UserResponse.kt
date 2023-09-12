package project.kotlin_board.dto.response

import project.kotlin_board.model.entity.User

data class UserResponse(
    val email: String,
    val username: String
) {
    companion object {
        fun fromEntity(entity: User): UserResponse {
            return UserResponse(
                email = entity.email,
                username = entity.username
            )
        }
    }
}
