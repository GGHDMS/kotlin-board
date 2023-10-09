package project.kotlin_board.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.Role
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.UserRepository
import project.kotlin_board.security.jwt.JwtGenerator

@DisplayName("유저 서비스 테스트")
@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    lateinit var sut: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var jwtGenerator: JwtGenerator

    @Spy
    lateinit var encoder: BCryptPasswordEncoder

    @Test
    @DisplayName("새로운 유저 정보를 입력하면, 유저를 가입시킨다.")
    fun givenNewUserInfo_whenRequestingSignUp_thenUserIsRegistered() {
        //given
        val request = createSignupRequest()
        val createdUser = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(null)
        given(userRepository.save(any(User::class.java))).willReturn(createdUser)

        //when
        val result = sut.signUp(request)

        //then
        assertThat(result.email).isEqualTo(request.email)
        assertThat(result.username).isEqualTo(request.username)

        then(userRepository).should().save(any(User::class.java))
    }

    @Test
    @DisplayName("존재하는 유저 정보를 입력하면, 예외를 반환한다.")
    fun givenExistingUserInfo_whenRequestingSignUp_thenReturnException() {
        //given
        val request = createSignupRequest()
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)

        //when & then
        assertThatThrownBy { sut.signUp(request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.DUPLICATED_EMAIL)
    }

    @Test
    @DisplayName("일치하는 refreshToken 을 입력하면, 새로운 accessToken 을 반환한다.")
    fun givenValidRefreshToken_whenRequestingRefreshToken_thenReturnAccessToken() {
        //given
        val authorization = "Bearer originToken"
        val user = createUser(refreshToken = authorization.split(" ")[1])
        val userDto = createUserDto()

        val newAccessToken = "newAccessToken"
        val newRefreshToken = "newRefreshToken"
        given(userRepository.getReferenceById(userDto.id)).willReturn(user)
        given(jwtGenerator.generateAccessToken(user.email, user.role)).willReturn(newAccessToken)
        given(jwtGenerator.generateRefreshToken(user.email, user.role)).willReturn(newRefreshToken)

        // sut.refreshToken 호출
        val result = sut.refreshToken(authorization, userDto)

        // 원하는 값을 반환하는지 확인
        assertThat(result.accessToken).isEqualTo(newAccessToken)
        assertThat(result.refreshToken).isEqualTo(newRefreshToken)

        // userRepository.getReferenceById 및 jwtGenerator.generateAccessToken 및 jwtGenerator.generateRefreshToken이 호출되었는지 확인
        then(userRepository).should().getReferenceById(userDto.id)
        then(jwtGenerator).should().generateAccessToken(user.email, user.role)
        then(jwtGenerator).should().generateRefreshToken(user.email, user.role)
    }

    @Test
    @DisplayName("잘못된 refreshToken 을 입력하면, 예외를 반환한다.")
    fun givenInValidRefreshToken_whenRequestingRefreshToken_thenReturnException() {
        //given
        val authorization = "Bearer validToken"
        val user = createUser(refreshToken = "originToken")
        val userDto = createUserDto()

        given(userRepository.getReferenceById(userDto.id)).willReturn(user)

        //when & then
        assertThatThrownBy { sut.refreshToken(authorization, userDto) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN)
    }

    @Test
    @DisplayName("존재하는 유저 정보를 삭제 요청하면, 유저를 삭제한다.")
    fun givenExistingUserInfo_whenDeletingUser_thenDeleteUser() {
        //given
        val request = createUserDto()
        val user = createUser()

        given(userRepository.getReferenceById(request.id)).willReturn(user)

        //when
        sut.deleteUser(request)

        //then
        then(userRepository).should().delete(user)
    }

    private fun createSignupRequest(
        email: String = "email@email.com",
        password: String = "password",
        username: String = "username",
        role: Role = Role.USER
    ) = SignUpRequest(email, password, username, role)


    private fun createUser(
        userId: Long = 1L,
        email: String = "email@email.com",
        username: String = "username",
        password: String = "password",
        role: Role = Role.USER,
        refreshToken: String? = null
    ) = User(id = userId, email = email, username = username, password = password, role = role, refreshToken = refreshToken)

    private fun createUserDto(
        id: Long = 1L,
        email: String = "email@email.com",
        username : String = "username",
        role : Role = Role.USER
    ) = UserDto(id, email, username, role)

}
