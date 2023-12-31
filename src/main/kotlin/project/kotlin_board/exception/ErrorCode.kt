package project.kotlin_board.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "Email is duplicated"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "Email not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Password is invalid"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "RefreshToken is invalid"),

    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Article not founded"),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not founded"),

    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "Permission is invalid"),
}
