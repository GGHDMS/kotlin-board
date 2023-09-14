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
import project.kotlin_board.dto.request.ArticleDeleteRequest
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.dto.response.ArticleResponse
import project.kotlin_board.service.ArticleService


@DisplayName("게시글 컨트롤러 테스트")
@Import(SecurityConfig::class)
@WebMvcTest(ArticleController::class)
class ArticleControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var articleService: ArticleService

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @DisplayName("게시글 생성 - 정상 요청")
    @Test
    fun givenArticle_whenCreatingArticle_thenCreateArticle() {
        //given
        val request = createArticleRequest()
        val response = createArticleResponse()
        given(articleService.create(request)).willReturn(response)

        //when & then
        mvc.perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value(response.title))
            .andExpect(jsonPath("$.content").value(response.content))
    }


    @DisplayName("게시글 생성시 title 과 content 의 값은 Blank 이면 예외를 반환.")
    @ParameterizedTest
    @MethodSource("blankArticleProvider")
    fun givenBlankValue_whenCreatingArticle_thenReturnException(request: ArticleRequest) {
        // given:

        //when & then
        mvc.perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isBadRequest)

    }

    @DisplayName("게시글 수정 - 정상 요청")
    @Test
    fun givenArticle_whenUpdatingArticle_thenUpdateArticle() {
        //given
        val articleId = 1L
        val request = createArticleRequest()
        val response = createArticleResponse()
        given(articleService.update(articleId, request)).willReturn(response)

        //when & then
        mvc.perform(
            put("/api/articles/$articleId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.title").value(response.title))
            .andExpect(jsonPath("$.content").value(response.content))
    }

    @DisplayName("게시글 수정시 title 과 content 의 값은 Blank 이면 예외를 반환.")
    @ParameterizedTest
    @MethodSource("blankArticleProvider")
    fun givenBlankValue_whenUpdatingArticle_thenReturnException(request: ArticleRequest) {
        // given:
        val articleId = 1L

        //when & then
        mvc.perform(
            put("/api/articles/$articleId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isBadRequest)
    }


    @DisplayName("게시글 삭제- 정상 요청")
    @Test
    fun givenArticleDelete_whenDeletingArticle_thenDeleteArticle() {
        //given
        val articleId = 1L
        val request = createDeleteArticleRequest()
        willDoNothing().given(articleService).delete(articleId, request)

        //when & then
        mvc.perform(
            delete("/api/articles/$articleId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isOk)
    }

    companion object {
        @JvmStatic
        fun blankArticleProvider() = listOf(
            Arguments.of(ArticleRequest("email@email.com", "password", "", "content")),
            Arguments.of(ArticleRequest("email@email.com", "password", " ", "content")),
            Arguments.of(ArticleRequest("email@email.com", "password", "title", " ")),
            Arguments.of(ArticleRequest("email@email.com", "password", "title", "")),
            Arguments.of(ArticleRequest("email@email.com", "password", "", "")),
            Arguments.of(ArticleRequest("email@email.com", "password", " ", " "))
        )
    }

    private fun createArticleRequest(
        email: String = "email@email.com",
        password: String = "password",
        title: String = "title",
        content: String = "content"
    ) = ArticleRequest(email, password, title, content)

    private fun createArticleResponse(
        id: Long = 1L,
        email: String = "email@email.com",
        title: String = "title",
        content: String = "content"
    ) = ArticleResponse(id, email, title, content)

    private fun createDeleteArticleRequest(
        email : String = "email@email.com",
        password: String = "password"
    ) = ArticleDeleteRequest(email, password)
}
