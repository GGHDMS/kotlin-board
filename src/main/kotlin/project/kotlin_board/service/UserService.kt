package project.kotlin_board.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.request.DeleteUserRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.UserResponse
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.UserRepository


@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder
) {

    fun signUp(request: SignUpRequest): UserResponse {
        //중복된 회원이 있는지 확인 필요함
        userRepository.findByEmail(request.email)?.let {
            throw BoardApplicationException(ErrorCode.DUPLICATED_EMAIL)
        }

        //저장하는 로직 필요함
        val hashedPassword = encoder.encode(request.password)

        // Create a User entity with the hashed password
        val user = User(
            email = request.email,
            password = hashedPassword,  // Use the hashed password here
            username = request.username
        )

        // Save the User entity
        val savedUser = userRepository.save(user)

        return UserResponse(
            email = savedUser.email,
            username = savedUser.username
        )
    }

    fun deleteUser(request: DeleteUserRequest) {
        // 존재하는 회원인지 확인 필요
        val user =
            userRepository.findByEmail(request.email) ?: throw BoardApplicationException(ErrorCode.EMAIL_NOT_FOUND)

        // 비밀번호 체크
        if (!encoder.matches(request.password, user.password)) {
            throw BoardApplicationException(ErrorCode.INVALID_PASSWORD)
        }

        // 유저 삭제
        userRepository.delete(user)
    }


}
