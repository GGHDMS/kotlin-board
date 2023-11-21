package project.kotlin_board.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.kotlin_board.dto.request.ShowRequest
import project.kotlin_board.dto.response.UserWithTimeResponse
import project.kotlin_board.service.AdminService
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class AdminController(
    private val adminService: AdminService,
) {

    @GetMapping("/show")
    fun searchUser(
        @ModelAttribute @Valid
        request: ShowRequest,
    ): List<UserWithTimeResponse> {
        return adminService.searchUser(
            request.username,
            request.email,
            request.createdAtStart,
            request.createdAtEnd,
            request.updatedAtStart,
            request.updatedAtEnd,
        )
    }
}
