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
import project.kotlin_board.dto.request.DeleteUserRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.UserRepository

@DisplayName("유저 서비스 테스트")
@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    lateinit var sut: UserService

    @Mock
    lateinit var userRepository: UserRepository

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
    @DisplayName("존재하는 유저 정보를 삭제 요청하면, 유저를 삭제한다.")
    fun givenExistingUserInfo_whenDeletingUser_thenDeleteUser() {
        //given
        val request = createDeleteUserRequest()
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)

        //when
        sut.deleteUser(request)

        //then
        then(userRepository).should().delete(user)
    }

    @Test
    @DisplayName("존재하지 않는 유저 정보를 삭제 요청하면, 예외를 반환한다")
    fun givenNonExistingUserInfo_whenDeletingUser_thenThrowException() {
        //given
        val request = createDeleteUserRequest()

        given(userRepository.findByEmail(request.email)).willReturn(null)

        //when & then
        assertThatThrownBy { sut.deleteUser(request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.EMAIL_NOT_FOUND)
    }

    @Test
    @DisplayName("잘못된 패스워드로 삭제 요청하면, 예외를 반환한다.")
    fun givenWrongPassword_whenDeletingUser_thenThrowException() {
        //given
        val request = createDeleteUserRequest(password = "wrongPassword")
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)

        //when & then
        assertThatThrownBy { sut.deleteUser(request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PASSWORD)
    }


    private fun createSignupRequest(
        email: String = "email@email.com",
        password: String = "password",
        username: String = "username"
    ) = SignUpRequest(email, password, username)

    private fun createDeleteUserRequest(
        email: String = "email@email.com",
        password: String = "password"
    ) = DeleteUserRequest(email, password)

    private fun createUser(
        userId: Long = 1L,
        email: String = "email@email.com",
        username: String = "username",
        password: String = "password"
    ) = User(userId, email, username, password)

}
