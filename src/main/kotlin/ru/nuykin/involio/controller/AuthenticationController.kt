package ru.nuykin.involio.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import ru.nuykin.involio.util.security.AuthRequest
import ru.nuykin.involio.util.security.AuthResponse
import ru.nuykin.involio.util.security.JWTUtil


@RestController
@Api(description = "Контроллер авторизации")
class AuthenticationController {
    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val jwtTokenUtil: JWTUtil? = null

    @PostMapping("/authenticate")
    @ApiOperation("Аутентификация пользователей")
    @ResponseStatus(HttpStatus.OK)
    fun createAuthenticationToken(@RequestBody authRequest: AuthRequest): AuthResponse {
        val authentication: Authentication
        try {
            println( authenticationManager?.authenticate(UsernamePasswordAuthenticationToken(
                authRequest.login,
                authRequest.password
            )))
            authentication = authenticationManager!!.authenticate(
                UsernamePasswordAuthenticationToken(
                    authRequest.login,
                    authRequest.password
                )
            )
            System.out.println(authentication)
        } catch (e: BadCredentialsException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e)
        }
        // при создании токена в него кладется username как Subject и список authorities как кастомный claim
        val jwt = jwtTokenUtil!!.generateToken((authentication.getPrincipal() as UserDetails))
        return AuthResponse(jwt)
    }
}