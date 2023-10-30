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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import project.kotlin_board.annotation.WithCustomMockUser
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.SignInRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.SignInResponse
import project.kotlin_board.dto.response.UserResponse
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.Role
import project.kotlin_board.service.UserService

@DisplayName("유저 컨트롤러 테스트")
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @DisplayName("로그인 - 정상 호출")
    @Test
    fun givenSignInRequest_whenRequestingSignIn_thenReturnSignInResponse() {
        // given
        val request = createSignInRequest()
        val response = createSignInResponse()

        given(userService.signIn(request)).willReturn(response)

        // when
        val result = mvc.perform(
            post("/api/users/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)),
        )

        // then
        result
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value(response.accessToken))
            .andExpect(jsonPath("$.refreshToken").value(response.refreshToken))
            .andExpect(jsonPath("$.email").value(response.email))
    }

    @DisplayName("회원 가입 - 정상 호출")
    @Test
    fun givenSignUpRequest_whenRequestingSignUp_thenReturnUserResponse() {
        // given
        val request = createSignupRequest()
        val response = createUserResponse()

        given(userService.signUp(request)).willReturn(response)

        // when & then
        mvc.perform(
            post("/api/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)),
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.username").value(response.username))
    }

    @DisplayName("회원 가입시 이미 가입된 이메일로 가입 요청시 에러 반환")
    @Test
    fun givenExistingEmail_whenRequestingSignUp_thenReturnError() {
        // given
        val request = createSignupRequest()
        given(userService.signUp(request)).willThrow(BoardApplicationException(ErrorCode.DUPLICATED_EMAIL))

        mvc.perform(
            post("/api/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)),
        ).andExpect(status().isConflict())
    }

    @DisplayName("회원 가입시 email 과 password 의 값이 잘못된 형식이거나 Blank 이면 예외를 반환.")
    @ParameterizedTest
    @MethodSource("wrongSignupProvider")
    fun givenWrongValue_whenRequestingSignUp_thenReturnException(request: SignUpRequest) {
        // given:

        // when & then
        mvc.perform(
            post("/api/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @DisplayName("존재하는 유저 삭제 요청 - 정상 호출")
    @Test
    @WithCustomMockUser
    fun givenExistingUserInfo_whenDeletingUser_thenDeleteUser() {
        val request = createUserDto()
        willDoNothing().given(userService).deleteUser(request)

        mvc.perform(
            delete("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)),
        ).andExpect(status().isOk())
    }

    companion object {
        @JvmStatic
        fun wrongSignupProvider() = listOf(
            Arguments.of(SignUpRequest("", "password", "username", Role.USER)),
            Arguments.of(SignUpRequest(" ", "password", "username", Role.USER)),
            Arguments.of(SignUpRequest("email@email.com", "", "username", Role.USER)),
            Arguments.of(SignUpRequest("email@email.com", " ", "username", Role.USER)),
            Arguments.of(SignUpRequest("email", "password", "username", Role.USER)),
            Arguments.of(SignUpRequest("email@", "password", "username", Role.USER)),
        )
    }

    private fun createSignInRequest(
        email: String = "email@email.com",
        password: String = "password",
    ) = SignInRequest(email, password)

    private fun createSignInResponse(
        email: String = "email@email.com",
        username: String = "username",
        role: Role = Role.USER,
        accessToken: String = "accessToken",
        refreshToken: String = "refreshToken",
    ) = SignInResponse(email, username, role, accessToken, refreshToken)

    private fun createSignupRequest(
        email: String = "email@email.com",
        password: String = "password",
        username: String = "username",
        role: Role = Role.USER,
    ) = SignUpRequest(email, password, username, Role.USER)

    private fun createUserResponse(
        email: String = "email@email.com",
        username: String = "username",
        role: Role = Role.USER,
    ) = UserResponse(email, username, Role.USER)

    private fun createUserDto(
        id: Long = 1L,
        email: String = "email@email.com",
        username: String = "username",
        role: Role = Role.USER,
    ) = UserDto(id, email, username, role)
}
