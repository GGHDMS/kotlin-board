package project.kotlin_board.exception

class BoardApplicationException(
    val errorCode: ErrorCode,
    customMessage: String? = null,
) : RuntimeException(
    customMessage ?: errorCode.message,
)
