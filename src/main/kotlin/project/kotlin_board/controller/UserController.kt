package project.kotlin_board.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import project.kotlin_board.dto.request.DeleteUserRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.UserResponse
import project.kotlin_board.service.UserService

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun signUp(@RequestBody signUpRequest: SignUpRequest) : UserResponse {
        return userService.signUp(signUpRequest)
    }

    @DeleteMapping
    fun deleteUser(@RequestBody deleteUserRequest: DeleteUserRequest) {
        return userService.deleteUser(deleteUserRequest)
    }

}
