package project.kotlin_board.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import project.kotlin_board.dto.request.CommentDeleteRequest
import project.kotlin_board.dto.request.CommentRequest
import project.kotlin_board.exception.BoardApplicationException
import project.kotlin_board.exception.ErrorCode
import project.kotlin_board.model.entity.Article
import project.kotlin_board.model.entity.Comment
import project.kotlin_board.model.entity.User
import project.kotlin_board.model.repository.ArticleRepository
import project.kotlin_board.model.repository.CommentRepository
import project.kotlin_board.model.repository.UserRepository
import java.util.*

@DisplayName("댓글 서비스 테스트")
@ExtendWith(MockitoExtension::class)
class CommentServiceTest {

    @InjectMocks
    lateinit var sut: CommentService

    @Mock
    lateinit var commentRepository: CommentRepository

    @Mock
    lateinit var articleRepository: ArticleRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var encoder: BCryptPasswordEncoder


    @DisplayName("댓글 생성 - 정상 요청")
    @Test
    fun givenCommentInfo_whenCreatingComment_thenCreateComment() {
        //given
        val request = createCommentRequest()
        val user = createUser()
        
        val article = createArticle()
        val articleId = article.id

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))
        given(commentRepository.save(any(Comment::class.java))).willReturn(createComment())

        //when
        val result = sut.create(articleId, request)

        //then
        then(userRepository).should().findByEmail(request.email)
        then(articleRepository).should().findById(articleId)
        then(commentRepository).should().save(any(Comment::class.java))

        assertThat(result.content).isEqualTo(request.content)
        assertThat(result.email).isEqualTo(request.email)
    }

    @DisplayName("존재 하지 않는 유저가 댓글 생성 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingUser_whenCreatingComment_thenReturnException() {
        //given
        val articleId = 1L
        val request = createCommentRequest()

        given(userRepository.findByEmail(request.email)).willReturn(null)

        //when & then
        assertThatThrownBy { sut.create(articleId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.EMAIL_NOT_FOUND)
    }

    @DisplayName("비밀 번호가 틀린 유저가 댓글 생성 요청시 예외를 반환한다.")
    @Test
    fun givenWrongPassword_whenCreatingComment_thenReturnException() {
        //given
        val articleId = 1L
        val request = createCommentRequest(password = "wrongPassword")
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(false)

        //when & then
        assertThatThrownBy { sut.create(articleId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PASSWORD)
    }

    @DisplayName("존재하지 않는 게시글로 댓글 생성 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingArticle_whenCreatingComment_thenReturnException() {
        //given
        val articleId = 1L
        val request = createCommentRequest()
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.empty())

        //when & then
        assertThatThrownBy { sut.create(articleId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.ARTICLE_NOT_FOUND)
    }

    @DisplayName("댓글 수정 - 정상 요청")
    @Test
    fun givenCommentInfo_whenUpdatingComment_thenUpdateComment() {
        //given
        val request = createCommentRequest()
        val user = createUser()
        
        val article = createArticle()
        val articleId = article.id
        
        val originalComment = createComment(content = "Original Content")
        val updatedComment = createComment(content = request.content)
        val commentId = originalComment.id

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))
        given(commentRepository.findByIdAndArticleId(commentId, articleId)).willReturn(originalComment)
        given(commentRepository.save(any(Comment::class.java))).willReturn(updatedComment)

        //when
        val result = sut.update(articleId, commentId, request)

        //then
        then(userRepository).should().findByEmail(request.email)
        then(articleRepository).should().findById(articleId)
        then(commentRepository).should().findByIdAndArticleId(commentId, articleId)
        then(commentRepository).should().save(any(Comment::class.java))

        assertThat(result.content).isEqualTo(request.content)
        assertThat(result.email).isEqualTo(request.email)
    }

    @DisplayName("존재 하지 않는 유저가 댓글 수정 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingUser_whenUpdatingComment_thenReturnException() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createCommentRequest()

        given(userRepository.findByEmail(request.email)).willReturn(null)

        //when & then
        assertThatThrownBy { sut.update(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.EMAIL_NOT_FOUND)

    }

    @DisplayName("비밀 번호를 틀린 유저가 댓글 수정 요청시 예외를 반환한다.")
    @Test
    fun givenWrongPassword_whenUpdatingComment_thenReturnException() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createCommentRequest(password = "wrongPassword")
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(false)

        //when & then
        assertThatThrownBy { sut.update(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PASSWORD)
    }

    @DisplayName("존재 하지 않는 게시글로 댓글 수정 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingArticle_whenUpdatingComment_thenReturnException() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createCommentRequest()
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.empty())

        //when & then
        assertThatThrownBy { sut.update(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.ARTICLE_NOT_FOUND)
    }

    @DisplayName("존재 하지 않는 댓글로 수정 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingComment_whenUpdatingComment_thenReturnException() {
        //given
        val commentId = 1L
        val request = createCommentRequest()
        val user = createUser()
        
        val article = createArticle()
        val articleId = article.id

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))
        given(commentRepository.findByIdAndArticleId(commentId, articleId)).willReturn(null)

        //when & then
        assertThatThrownBy { sut.update(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.COMMENT_NOT_FOUND)
    }

    @DisplayName("작성자가 아닌 유저가 수정 요청하면, 예외를 반환한다.")
    @Test
    fun givenDifferUser_whenUpdatingComment_thenReturnException() {
        //given
        val request = createCommentRequest()

        val originalAuthor = createUser(id = 2L, email = "original@email.com")
        val requestUser = createUser()

        val article = createArticle()
        val articleId = article.id
        
        val comment = createComment(user = originalAuthor)
        val commentId = comment.id

        given(userRepository.findByEmail(request.email)).willReturn(requestUser)
        given(encoder.matches(request.password, requestUser.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))
        given(commentRepository.findByIdAndArticleId(commentId, articleId)).willReturn(comment)

        //then & then
        assertThatThrownBy { sut.update(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PERMISSION)
    }

    @DisplayName("댓글 삭제 요청 - 정상 호출")
    @Test
    fun givenComment_whenDeletingComment_thenDeleteComment() {
        //given
        val request = createCommentDeleteRequest()
        val user = createUser()
        
        val article = createArticle()
        val articleId = article.id
        
        val comment = createComment()
        val commentId = comment.id

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))
        given(commentRepository.findByIdAndArticleId(commentId, articleId)).willReturn(comment)
        willDoNothing().given(commentRepository).delete(comment)

        //when
        val result = sut.delete(articleId, commentId, request)

        //then
        then(userRepository).should().findByEmail(request.email)
        then(articleRepository).should().findById(articleId)
        then(commentRepository).should().findByIdAndArticleId(commentId, articleId)
        then(commentRepository).should().delete(comment)
    }

    @DisplayName("존재하지 않는 유저가 댓글 삭제 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingUser_whenDeletingComment_thenReturnException() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createCommentDeleteRequest()

        given(userRepository.findByEmail(request.email)).willReturn(null)

        //when & then
        assertThatThrownBy { sut.delete(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.EMAIL_NOT_FOUND)
    }

    @DisplayName("비밀 번호를 틀린 유저가 댓글 삭제 요청시 예외를 반환한다.")
    @Test
    fun givenWrongPassword_whenDeletingComment_thenReturnException() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createCommentDeleteRequest(password = "wrongPassword")
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(false)

        //when & then
        assertThatThrownBy { sut.delete(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PASSWORD)
    }

    @DisplayName("존재하지 않는 게시글로 댓글 삭제 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingArticle_whenDeletingComment_thenReturnException() {
        //given
        val articleId = 1L
        val commentId = 1L
        val request = createCommentDeleteRequest()
        val user = createUser()

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.empty())

        //when & then
        assertThatThrownBy { sut.delete(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.ARTICLE_NOT_FOUND)
    }

    @DisplayName("존재하지 않는 댓글을 삭제 요청시 예외를 반환한다.")
    @Test
    fun givenNonExistingComment_whenDeletingComment_thenReturnException() {
        //given
        val commentId = 1L
        val request = createCommentDeleteRequest()
        val user = createUser()
        
        val article = createArticle()
        val articleId = article.id

        given(userRepository.findByEmail(request.email)).willReturn(user)
        given(encoder.matches(request.password, user.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))
        given(commentRepository.findByIdAndArticleId(commentId, articleId)).willReturn(null)

        //when & then
        assertThatThrownBy { sut.delete(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.COMMENT_NOT_FOUND)
    }

    @DisplayName("작성자가 아닌 유저가 댓글 삭제 요청시 예외를 반환한다.")
    @Test
    fun givenDifferUser_whenDeletingComment_thenReturnException() {
        //given
        val request = createCommentDeleteRequest()

        val originalAuthor = createUser(2L, "original@email.com")
        val requestUser = createUser()

        val article = createArticle()
        val articleId = article.id
        
        val comment = createComment(user = originalAuthor)
        val commentId = comment.id

        given(userRepository.findByEmail(request.email)).willReturn(requestUser)
        given(encoder.matches(request.password, requestUser.password)).willReturn(true)
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article))
        given(commentRepository.findByIdAndArticleId(commentId, articleId)).willReturn(comment)

        //then & then
        assertThatThrownBy { sut.delete(articleId, commentId, request) }
            .isInstanceOf(BoardApplicationException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INVALID_PERMISSION)
    }

    private fun createCommentRequest(
        email: String = "email@email.com",
        password: String = "password",
        content: String = "content"
    ) = CommentRequest(email, password, content)

    private fun createUser(
        id: Long = 1L,
        email: String = "email@email.com",
        username: String = "username",
        password: String = "password"
    ) = User(id, email, username, password)

    private fun createArticle(
        articleId: Long = 1L,
        title: String = "title",
        content: String = "content",
        user: User = createUser()
    ) = Article(articleId, title, content, user)

    private fun createComment(
        id: Long = 1L,
        content: String = "content",
        user: User = createUser(),
        article: Article = createArticle()
    ) = Comment(id, content, user, article)

    private fun createCommentDeleteRequest(
        email: String = "email@email.com",
        password: String = "password"
    ) = CommentDeleteRequest(email, password)
}
