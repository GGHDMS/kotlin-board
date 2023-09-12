package project.kotlin_board.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import project.kotlin_board.dto.request.DeleteUserRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.UserResponse
import project.kotlin_board.exception.DuplicatedEmailException
import project.kotlin_board.service.UserService

@DisplayName("유저 컨트롤러 테스트")
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
        val request = createSignupRequest("email@email.com")
        val response = createUserResponse("email@email.com")

        given(userService.signUp(request)).willReturn(response)

        //when & then
        mvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(response))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.username").value(response.username))
    }

    @DisplayName("회원 가입시 이미 가입된 이메일로 가입 요청시 에러 반환")
    @Test
    fun givenExistingEmail_whenRequestingSignUp_thenReturnError() {
        //given
        val request = createSignupRequest("email@email.com")
        given(userService.signUp(request)).willThrow(DuplicatedEmailException::class.java)

        mvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict())
    }

    @DisplayName("존재하는 유저 삭제 요청 - 정상 호출")
    @Test
    fun givenExistingUserInfo_whenDeletingUser_thenDeleteUser() {
        val request = createDeleteUserRequest("email@email.com")
        willDoNothing().given(userService).deleteUser(request)

        mvc.perform(
            delete("/api/users")
        ).andExpect(status().isOk())
    }

    private fun createSignupRequest(email: String): SignUpRequest {
        return SignUpRequest(email, "password", "username")
    }

    private fun createDeleteUserRequest(email: String): DeleteUserRequest {
        return DeleteUserRequest(email, "password")
    }

    private fun createUserResponse(email: String): UserResponse {
        return UserResponse(email, "username")
    }
}
