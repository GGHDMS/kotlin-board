package project.kotlin_board.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.SignInRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.RefreshResponse
import project.kotlin_board.dto.response.SignInResponse
import project.kotlin_board.dto.response.UserResponse
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.UserRepository
import project.kotlin_board.security.jwt.JwtGenerator

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder,
    private val jwtGenerator: JwtGenerator,
) {

    fun signUp(request: SignUpRequest): UserResponse {
        // 중복된 회원이 있는지 확인 필요함
        userRepository.findByEmail(request.email)?.let {
            throw BoardApplicationException(ErrorCode.DUPLICATED_EMAIL)
        }

        // 저장하는 로직 필요함
        val hashedPassword = encoder.encode(request.password)

        val user = User(
            email = request.email,
            password = hashedPassword, // Use the hashed password here
            username = request.username,
            role = request.role,
        )

        // Save the User entity
        val savedUser = userRepository.save(user)

        return UserResponse(
            email = savedUser.email,
            username = savedUser.username,
            role = savedUser.role,
        )
    }

    fun signIn(request: SignInRequest): SignInResponse {
        // 존재하는 회원인지 확인 필요
        val user =
            userRepository.findByEmail(request.email) ?: throw BoardApplicationException(ErrorCode.EMAIL_NOT_FOUND)

        // 비밀번호 체크
        if (!encoder.matches(request.password, user.password)) {
            throw BoardApplicationException(ErrorCode.INVALID_PASSWORD)
        }

        val accessToken = jwtGenerator.generateAccessToken(user.email, user.role)
        val refreshToken = jwtGenerator.generateRefreshToken(user.email, user.role)

        user.saveRefreshToken(refreshToken)

        userRepository.save(user)

        return SignInResponse(
            email = user.email,
            username = user.username,
            role = user.role,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    fun refreshToken(authorization: String, userDto: UserDto): RefreshResponse {
        val user = userRepository.getReferenceById(userDto.id)
        val refreshToken = authorization.split(" ")[1]

        if (user.refreshToken != refreshToken) {
            user.refreshToken = null
            userRepository.save(user)

            throw BoardApplicationException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val accessToken = jwtGenerator.generateAccessToken(user.email, user.role)
        val newRefreshToken = jwtGenerator.generateRefreshToken(user.email, user.role)

        user.saveRefreshToken(newRefreshToken)

        userRepository.save(user)

        return RefreshResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken,
        )
    }

    fun deleteUser(userDto: UserDto) {
        val user = userRepository.getReferenceById(userDto.id)

        // 유저 삭제
        userRepository.delete(user)
    }
}
