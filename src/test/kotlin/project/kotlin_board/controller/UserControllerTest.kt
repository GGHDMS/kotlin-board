package project.kotlin_board.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import project.kotlin_board.config.SecurityConfig
import project.kotlin_board.dto.request.DeleteUserRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.UserResponse
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.service.UserService

@DisplayName("유저 컨트롤러 테스트")
@Import(SecurityConfig::class)
@WebMvcTest(UserController::class)
class UserControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @DisplayName("회원 가입 - 정상 호출")
    @Test
    fun givenSignUpRequest_whenRequestingSignUp_thenReturnUserResponse() {
        //given
        val request = createSignupRequest()
        val response = createUserResponse()

        given(userService.signUp(request)).willReturn(response)

        //when & then
        mvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.username").value(response.username))
    }

    @DisplayName("회원 가입시 이미 가입된 이메일로 가입 요청시 에러 반환")
    @Test
    fun givenExistingEmail_whenRequestingSignUp_thenReturnError() {
        //given
        val request = createSignupRequest()
        given(userService.signUp(request)).willThrow(BoardApplicationException(ErrorCode.DUPLICATED_EMAIL))

        mvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        ).andExpect(status().isConflict())
    }

    @DisplayName("회원 가입시 email 과 password 의 값이 잘못된 형식이거나 Blank 이면 예외를 반환.")
    @ParameterizedTest
    @MethodSource("wrongSignupProvider")
    fun givenWrongValue_whenRequestingSignUp_thenReturnException(request: SignUpRequest) {
        // given:

        //when & then
        mvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isBadRequest)

    }

    @DisplayName("존재하는 유저 삭제 요청 - 정상 호출")
    @Test
    fun givenExistingUserInfo_whenDeletingUser_thenDeleteUser() {
        val request = createDeleteUserRequest()
        willDoNothing().given(userService).deleteUser(request)

        mvc.perform(
            delete("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        ).andExpect(status().isOk())
    }

    companion object {
        @JvmStatic
        fun wrongSignupProvider() = listOf(
            Arguments.of(SignUpRequest("","password", "username")),
            Arguments.of(SignUpRequest(" ", "password", "username")),
            Arguments.of(SignUpRequest("email@email.com", "", "username")),
            Arguments.of(SignUpRequest("email@email.com", " ", "username")),
            Arguments.of(SignUpRequest("email", "password", "username")),
            Arguments.of(SignUpRequest("email@", "password", "username"))
        )
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

    private fun createUserResponse(
        email: String = "email@email.com",
        username: String = "username"
    ) = UserResponse(email, username)

}
