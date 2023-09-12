package project.kotlin_board.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import project.kotlin_board.dto.request.DeleteUserRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.exception.DuplicatedEmailException
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.UserRepository

@DisplayName("유저 서비스 테스트")
@ExtendWith(MockitoExtension::class)
class UserServiceTest (
    @InjectMocks
    val sut: UserService,
    @Mock
    var userRepository: UserRepository
){

    @Test
    @DisplayName("새로운 유저 정보를 입력하면, 유저를 가입시킨다.")
    fun givenNewUserInfo_whenRequestingSignUp_thenUserIsRegistered() {
        //given
        val signUpRequest = createSignupRequest("email@email.com")
        val createdUser = User(1L, "email@email.com", "password", "username")

        given(userRepository.findByEmail(signUpRequest.email)).willReturn(null)
        given(userRepository.save(signUpRequest.toEntity())).willReturn(createdUser)

        //when
        val result = sut.signUp(signUpRequest)

        //then
        assertThat(result)
            .hasFieldOrPropertyWithValue("email", signUpRequest.email)
            .hasFieldOrPropertyWithValue("username", signUpRequest.username)

        then(userRepository).should().save(signUpRequest.toEntity())
    }

    @Test
    @DisplayName("존재하는 유저 정보를 입력하면, 에러를 발생시킨다.")
    fun givenExistingUserInfo_whenRequestingSignUp_thenThrowError() {
        //given
        val signUpRequest = createSignupRequest("eamil@email.com")
        val existingUser = User(1L, "email@email.com", "password", "username")

        given(userRepository.findByEmail(signUpRequest.email)).willReturn(existingUser)

        //when & then
        assertThatThrownBy { sut.signUp(signUpRequest) }
            .isInstanceOf(DuplicatedEmailException::class.java)
    }

    @Test
    @DisplayName("존재하는 유저 정보를 삭제 요청하면, 유저를 삭제한다.")
    fun givenExistingUserInfo_whenDeletingUser_thenDeleteUser() {
        //given
        val deleteUserRequest = DeleteUserRequest("email@email.com", "password")

        //when
        sut.deleteUser(deleteUserRequest)

        //then
        then(userRepository).should().delete(any(User::class.java))
    }

    private fun createSignupRequest(email: String): SignUpRequest {
        return SignUpRequest(email, "password", "username")
    }

}
