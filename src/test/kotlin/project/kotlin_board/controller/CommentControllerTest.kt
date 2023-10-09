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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import project.kotlin_board.annotation.WithCustomMockUser
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.CommentRequest
import project.kotlin_board.dto.response.CommentResponse
import project.kotlin_board.model.Role
import project.kotlin_board.service.CommentService

@DisplayName("댓글 컨트롤러 테스트")
@AutoConfigureMockMvc
@SpringBootTest
class CommentControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var commentService : CommentService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @DisplayName("댓글 생성 - 정상 요청")
    @Test
    @WithCustomMockUser
    fun givenComment_whenCreatingComment_thenCreateComment() {
        //given
        val articleId = 1L
        val request = createCommentRequest()
        val response = createCommentResponse()
        val userDto = createUserDto()
        given(commentService.create(articleId, request, userDto)).willReturn(response)

        //when & then
        mvc.perform(
            post("/api/articles/$articleId/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.content").value(response.content))
    }


    @DisplayName("댓글 생성시 title 과 content 의 값은 Blank 이면 예외를 반환.")
    @ParameterizedTest
    @MethodSource("blankCommentProvider")
    @WithCustomMockUser
    fun givenBlankValue_whenCreatingComment_thenReturnException(request: CommentRequest) {
        // given:
        val articleId = 1L

        //when & then
        mvc.perform(
            post("/api/articles/$articleId/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isBadRequest)

    }

    @DisplayName("댓글 수정 - 정상 요청")
    @Test
    @WithCustomMockUser
    fun givenComment_whenUpdatingComment_thenUpdateComment() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createCommentRequest()
        val response = createCommentResponse()
        val userDto = createUserDto()

        given(commentService.update(articleId, commentId, request, userDto)).willReturn(response)

        //when & then
        mvc.perform(
            put("/api/articles/$articleId/comments/$commentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.content").value(response.content))
    }

    @DisplayName("댓글 수정시 title 과 content 의 값은 Blank 이면 예외를 반환.")
    @ParameterizedTest
    @MethodSource("blankCommentProvider")
    @WithCustomMockUser
    fun givenBlankValue_whenUpdatingComment_thenReturnException(request: CommentRequest) {
        // given:
        val articleId = 1L
        val commentId = 1L

        //when & then
        mvc.perform(
            put("/api/articles/$articleId/comments/$commentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isBadRequest)
    }


    @DisplayName("댓글 삭제- 정상 요청")
    @Test
    @WithCustomMockUser
    fun givenCommentDelete_whenDeletingComment_thenDeleteComment() {
        //given
        val articleId = 1L
        val commentId = 1L
        val userDto = createUserDto()
        willDoNothing().given(commentService).delete(articleId, commentId, userDto)

        //when & then
        mvc.perform(
            delete("/api/articles/$articleId/comments/$commentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userDto))
        )
            .andExpect(status().isOk)
    }

    companion object {
        @JvmStatic
        fun blankCommentProvider() = listOf(
            Arguments.of(CommentRequest("")),
            Arguments.of(CommentRequest(" ")),
        )
    }

    private fun createCommentRequest(
        content: String = "content"
    ) = CommentRequest(content)

    private fun createCommentResponse(
        id: Long = 1L,
        email: String = "email@email.com",
        content: String = "content"
    ) = CommentResponse(id, email, content)

    private fun createUserDto(
        id: Long = 1L,
        email: String = "email@email.com",
        username : String = "username",
        role : Role = Role.USER
    ) = UserDto(id, email, username, role)
}
