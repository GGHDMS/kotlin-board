package project.kotlin_board.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.response.UserWithTimeResponse
import project.kotlin_board.model.repository.UserRepository
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class AdminService(
    private val userRepository: UserRepository,
) {

    fun searchUser(
        username: String?,
        email: String?,
        createdAtStart: LocalDate?,
        createdAtEnd: LocalDate?,
        updatedAtStart: LocalDate?,
        updatedAtEnd: LocalDate?,
    ): List<UserWithTimeResponse> {
        return userRepository.findUserListByQueryDsl(
            username,
            email,
            createdAtStart,
            createdAtEnd,
            updatedAtStart,
            updatedAtEnd,
        )
    }
}
