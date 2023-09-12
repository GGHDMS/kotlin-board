package project.kotlin_board.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.kotlin_board.dto.request.DeleteUserRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.UserResponse
import project.kotlin_board.service.UserService

@RestController
@RequestMapping("/api/users")
class UserController(
    userService: UserService
) {

    @PostMapping
    fun signUp(@RequestBody signUpRequest: SignUpRequest) : UserResponse {
        // 서비스 호출
        return UserResponse("", "")
    }

    @DeleteMapping
    fun deleteUser(@RequestBody deleteUserRequest: DeleteUserRequest) {
        // 서비스 호출
    }

}
