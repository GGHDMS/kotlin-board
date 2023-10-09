package project.kotlin_board.security.handler

import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import project.kotlin_board.dto.UserDto
import project.kotlin_board.security.annotation.LoginUser

@Component
class LoginUserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val isLoginUserAnnotation = parameter.hasParameterAnnotation(LoginUser::class.java)
        val isUserClass = UserDto::class.java.isAssignableFrom(parameter.parameterType)

        return isLoginUserAnnotation && isUserClass
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        with(SecurityContextHolder.getContext()) {
            if (authentication.principal != null && authentication.principal is UserDto) {
                return authentication.principal as UserDto
            }else throw RuntimeException("Authentication 의 principal 객체가 UserDto 타입이 아닙니다.")
        }
    }
}
