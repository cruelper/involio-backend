package ru.nuykin.involio.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.*
import ru.nuykin.involio.dto.MyUserDto
import ru.nuykin.involio.service.CustomUserDetailsService



@RestController
@Api(description = "Контроллер регистрации")
class RegistrationController {
    @Autowired
    private val customUserDetailsService: CustomUserDetailsService? = null

    @PostMapping("/user-registration")
    @ApiOperation("Регистрация нового пользователя")
    fun userRegistration(@RequestBody user: MyUserDto): Pair<String, HttpStatus> =
        customUserDetailsService!!.registration(user, isAdmin = false)

    @PostMapping("/admin/admin-registration")
    @ApiOperation("Регистрация нового администратора")
    fun adminRegistration(@RequestBody user: MyUserDto): Pair<String, HttpStatus> =
        customUserDetailsService!!.registration(user, isAdmin = true)
}