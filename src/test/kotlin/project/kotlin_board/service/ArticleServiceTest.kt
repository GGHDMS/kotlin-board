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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import project.kotlin_board.dto.request.ArticleDeleteRequest
import project.kotlin_board.dto.request.ArticleRequest
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.entity.Article
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.ArticleRepository
import project.kotlin_board.model.repository.UserRepository
import java.util.*

@DisplayName("게시글 서비스 테스트")
@ExtendWith(MockitoExtension::class)
class ArticleServiceTest {

    @InjectMocks
    lateinit var sut: ArticleService

    @Mock
    lateinit var articleRepository: ArticleRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var encoder: BCryptPasswordEncoder

    //TODO : findByIdOrNull 이 mockito 에 없어서 이 함수를 직접적으로 비교 할 수 없어 findById 를 사용해 Optional 을 이용해 테스트 작성

    @DisplayName("게시글 생성 요청 - 정상 동작")
    @Test
    fun givenArticleInfo_whenCreatingArticle_thenCreateArticle() {
        //given
        val request = createArticleRequest()
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.save(any(Article::class.java))).willReturn(createArticle())

        //when
        val result = sut.create(request)

        //then
        then(articleRepository).should().save(any(Article::class.java))
        assertThat(result.title).isEqualTo(request.title)
        assertThat(result.content).isEqualTo(request.content)
    }

    @DisplayName("가입되어 있지 않은 회원이 게시글을 작성하면, 예외를 반환한다.")
    @Test
    fun givenUnSignUpUser_whenCreatingArticle_thenReturnException() {
        //given
        val request = createArticleRequest()

        given(userRepository.findByEmail(request.email)).willReturn(null)

        //given & then
        assertThatThrownBy { sut.create(request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.EMAIL_NOT_FOUND)
    }

    @DisplayName("잘못된 패스워드로 게시글을 작성하면, 예외를 반환한다.")
    @Test
    fun givenWrongPassword_whenCreatingArticle_thenReturnException() {
        //given
        val request = createArticleRequest(password = "wrongPassword")
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(false)
        //when & then
        assertThatThrownBy { sut.create(request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PASSWORD)
    }

    @DisplayName("게시글 수정 요청 - 정상 동작")
    @Test
    fun givenArticle_whenUpdatingArticle_thenUpdateArticle() {
        //given
        val request = createArticleRequest()
        val user = createUser()
        val originalArticle = createArticle(title = "Original Title", content = "Original Content")
        val updatedArticle = createArticle()
        val articleId = originalArticle.id

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(originalArticle))
        given(articleRepository.save(any(Article::class.java))).willReturn(updatedArticle)

        //when
        val result = sut.update(articleId, request)

        //then
        then(articleRepository).should().save(any(Article::class.java))
        assertThat(result.title).isEqualTo(request.title)
        assertThat(result.content).isEqualTo(request.content)
    }

    @DisplayName("존재 하지 않는 게시글 수정 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingArticle_whenUpdatingArticle_thenReturnException() {
        //given
        val request = createArticleRequest()
        val user = createUser()
        val articleId = 1L

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.empty())

        //when & then
        assertThatThrownBy { sut.update(articleId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.ARTICLE_NOT_FOUND)
    }

    @DisplayName("게시글 작성자가 아닌 사람이 수정 요청하면, 예외를 반환한다.")
    @Test
    fun givenDifferUser_whenUpdatingArticle_thenReturnException() {
        //given
        val request = createArticleRequest()

        val originalAuthor = createUser(2L, "original@email.com")
        val requestUser = createUser()

        val article = createArticle(user = originalAuthor)
        val articleId = article.id

        given(userRepository.findByEmail(request.email)).willReturn(requestUser)
        given(encoder.matches(request.password, requestUser.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))

        //when & then
        assertThatThrownBy { sut.update(articleId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PERMISSION)
    }

    @DisplayName("게시글 삭제 - 정상 요청")
    @Test
    fun givenArticle_whenDeletingArticle_thenDeleteArticle() {
        //given
        val request = createDeleteArticle()
        val user = createUser()
        val article = createArticle()
        val articleId = article.id

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))
        willDoNothing().given(articleRepository).delete(article)

        //when
        sut.delete(articleId, request)

        //then
        then(articleRepository).should().delete(article)
    }

    @DisplayName("존재하지 않는 게시글 삭제 요청시, 예외를 반환한다.")
    @Test
    fun givenNonExistingArticle_whenDeletingArticle_thenReturnException() {
        //given
        val request = createDeleteArticle()
        val user = createUser()
        val articleId = 1L

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.empty())

        //when & then
        assertThatThrownBy { sut.delete(articleId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.ARTICLE_NOT_FOUND)
    }

    @DisplayName("게시글 작성자가 아닌 사람이 삭제 요청하면, 예외를 반환한다.")
    @Test
    fun givenDifferUser_whenDeletingArticle_thenReturnException() {
        //given
        val request = createDeleteArticle()

        val originalAuthor = createUser(2L, "original@email.com")
        val requestUser = createUser()

        val article = createArticle(originalAuthor)
        val articleId = article.id

        given(userRepository.findByEmail(request.email)).willReturn(requestUser)
        given(encoder.matches(request.password, requestUser.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))

        //when & then
        assertThatThrownBy { sut.delete(articleId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PERMISSION)
    }

    private fun createArticle(
        user: User = createUser(),
        title: String = "title",
        content: String = "content"
    ) = Article(1L, title, content, user)

    private fun createUser(
        userId: Long = 1L,
        email: String = "email@email.com",
        username: String = "username",
        password: String = "password"
    ) = User(userId, email, username, password)

    private fun createArticleRequest(
        email: String = "email@email.com",
        password: String = "password",
        title: String = "title",
        content: String = "content"
    ) = ArticleRequest(email, password, title, content)

    private fun createDeleteArticle(
        email: String = "email@email.com",
        password: String = "password"
    ) = ArticleDeleteRequest(email, password)

}
