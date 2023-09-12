package project.kotlin_board.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.kotlin_board.dto.request.DeleteUserRequest
import project.kotlin_board.dto.request.SignUpRequest
import project.kotlin_board.dto.response.UserResponse


@Service
@Transactional
class UserService(
) {

    fun signUp(signUpRequest: SignUpRequest): UserResponse {
        //중복된 회원이 있는지 확인 필요함

        //저장하는 로직 필요함
        return UserResponse(email = signUpRequest.email, username = signUpRequest.username)
    }

    fun deleteUser(deleteUserRequest: DeleteUserRequest) {
        // 존재하는 회원인지 확인 필요

        // 패스워드랑 이메일 일치 확인 필요

        // 유저 삭제
    }


}
