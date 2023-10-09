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
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.dto.response.ArticleResponse
import project.kotlin_board.model.Role
import project.kotlin_board.service.ArticleService


@DisplayName("게시글 컨트롤러 테스트")
@AutoConfigureMockMvc
@SpringBootTest
class ArticleControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var articleService: ArticleService

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @DisplayName("게시글 생성 - 정상 요청")
    @Test
    @WithCustomMockUser
    fun givenArticle_whenCreatingArticle_thenCreateArticle() {
        //given
        val request = createArticleRequest()
        val response = createArticleResponse()
        val userDto = createUserDto()

        given(articleService.create(request, userDto)).willReturn(response)

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
    @WithCustomMockUser
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
    @WithCustomMockUser
    fun givenArticle_whenUpdatingArticle_thenUpdateArticle() {
        //given
        val articleId = 1L
        val request = createArticleRequest()
        val response = createArticleResponse()
        val userDto = createUserDto()
        given(articleService.update(articleId, request, userDto)).willReturn(response)

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
    @WithCustomMockUser
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
    @WithCustomMockUser
    fun givenArticleDelete_whenDeletingArticle_thenDeleteArticle() {
        //given
        val articleId = 1L
        val userDto = createUserDto()
        willDoNothing().given(articleService).delete(articleId, userDto)

        //when & then
        mvc.perform(
            delete("/api/articles/$articleId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    companion object {
        @JvmStatic
        fun blankArticleProvider() = listOf(
            Arguments.of(ArticleRequest("", "content")),
            Arguments.of(ArticleRequest( " ", "content")),
            Arguments.of(ArticleRequest( "title", " ")),
            Arguments.of(ArticleRequest( "title", "")),
            Arguments.of(ArticleRequest( "", "")),
            Arguments.of(ArticleRequest( " ", " "))
        )
    }

    private fun createArticleRequest(
        title: String = "title",
        content: String = "content"
    ) = ArticleRequest(title, content)

    private fun createArticleResponse(
        id: Long = 1L,
        email: String = "email@email.com",
        title: String = "title",
        content: String = "content"
    ) = ArticleResponse(id, email, title, content)

    private fun createUserDto(
        id: Long = 1L,
        email: String = "email@email.com",
        username : String = "username",
        role : Role = Role.USER
    ) = UserDto(id, email, username, role)

}
