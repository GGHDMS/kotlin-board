package project.kotlin_board.dto.request

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import javax.validation.constraints.AssertTrue

data class ShowRequest(
    val username: String?,
    val email: String?,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val createdAtStart: LocalDate?,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val createdAtEnd: LocalDate?,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val updatedAtStart: LocalDate?,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val updatedAtEnd: LocalDate?,
) {
    @AssertTrue(message = "updatedAtEnd should be greater than or equal to updatedAtStart")
    private fun isValidUpdatedAtRange(): Boolean {
        if (updatedAtStart != null && updatedAtEnd != null) {
            return !updatedAtEnd.isBefore(updatedAtStart)
        }
        return true
    }

    @AssertTrue(message = "createdAtEnd should be greater than or equal to createdAtStart")
    private fun isValidCreatedAtRange(): Boolean {
        if (createdAtStart != null && createdAtEnd != null) {
            return !createdAtEnd.isBefore(createdAtStart)
        }
        return true
    }
}
