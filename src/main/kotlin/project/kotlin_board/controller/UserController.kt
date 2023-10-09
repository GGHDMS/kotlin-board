package project.kotlin_board.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import project.kotlin_board.dto.UserDto
import project.kotlin_board.dto.request.SignInRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.RefreshResponse
import project.kotlin_board.dto.response.SignInResponse
import project.kotlin_board.dto.response.UserResponse
import project.kotlin_board.security.annotation.LoginUser
import project.kotlin_board.service.UserService
import javax.validation.Valid

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    fun signUp(@Valid @RequestBody signUpRequest: SignUpRequest): UserResponse {
        return userService.signUp(signUpRequest)
    }

    @PostMapping("/sign-in")
    fun signIn(@Valid @RequestBody signInRequest: SignInRequest): SignInResponse {
        return userService.signIn(signInRequest)
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestHeader("Authorization") authorization: String,
        @LoginUser userDto: UserDto
    ): RefreshResponse {
        return userService.refreshToken(authorization, userDto)
    }

    @DeleteMapping
    fun deleteUser(@LoginUser userDto: UserDto) {
        userService.deleteUser(userDto)
    }

}
