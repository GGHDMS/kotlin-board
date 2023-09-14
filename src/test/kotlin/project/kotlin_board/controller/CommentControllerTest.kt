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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import project.kotlin_board.config.SecurityConfig
import project.kotlin_board.dto.request.CommentDeleteRequest
import project.kotlin_board.dto.request.CommentRequest
import project.kotlin_board.dto.response.CommentResponse
import project.kotlin_board.service.CommentService

@DisplayName("댓글 컨트롤러 테스트")
@Import(SecurityConfig::class)
@WebMvcTest(CommentController::class)
class CommentControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var commentService : CommentService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @DisplayName("댓글 생성 - 정상 요청")
    @Test
    fun givenComment_whenCreatingComment_thenCreateComment() {
        //given
        val articleId = 1L
        val request = createCommentRequest()
        val response = createCommentResponse()
        given(commentService.create(articleId, request)).willReturn(response)

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
    fun givenComment_whenUpdatingComment_thenUpdateComment() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createCommentRequest()
        val response = createCommentResponse()
        given(commentService.update(articleId, commentId, request)).willReturn(response)

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
    fun givenCommentDelete_whenDeletingComment_thenDeleteComment() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createDeleteCommentRequest()
        willDoNothing().given(commentService).delete(articleId, commentId, request)

        //when & then
        mvc.perform(
            delete("/api/articles/$articleId/comments/$commentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isOk)
    }

    companion object {
        @JvmStatic
        fun blankCommentProvider() = listOf(
            Arguments.of(CommentRequest("email@email.com", "password", "")),
            Arguments.of(CommentRequest("email@email.com", "password", " ")),
        )
    }

    private fun createCommentRequest(
        email: String = "email@email.com",
        password: String = "password",
        content: String = "content"
    ) = CommentRequest(email, password, content)

    private fun createCommentResponse(
        id: Long = 1L,
        email: String = "email@email.com",
        content: String = "content"
    ) = CommentResponse(id, email, content)

    private fun createDeleteCommentRequest(
        email : String = "email@email.com",
        password: String = "password"
    ) = CommentDeleteRequest(email, password)
}
