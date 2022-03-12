package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.MyUserDto
import ru.nuykin.involio.model.MyUser
import ru.nuykin.involio.repository.MyUserRepository


@Service
class CustomUserDetailsService : UserDetailsService {
    @Autowired
    private val dao: MyUserRepository? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String): UserDetails {
        val myUser: MyUser = dao?.findByLogin(userName) ?: throw UsernameNotFoundException("Unknown user: $userName")
        return User.builder()
            .username(myUser.login)
            .password(myUser.password)
            .roles(myUser.role)
            .build()
    }

    fun registration(user: MyUserDto, isAdmin: Boolean): Pair<String, Boolean>{
        if(dao?.findByIdOrNull(user.email) == null){
            if(dao?.findByLogin(user.login) == null){

                val newUser: MyUser = if(isAdmin)
                    MyUser(email = user.email, login = user.login, password = user.password, role = "ADMIN")
                else MyUser(email = user.email, login = user.login, password = user.password, role = "USER")

                dao?.save(newUser)

                return Pair("Регистрация прошла успешно!", true)
            } else return Pair("Данный логин занят!", false)
        }else return Pair("Данная электронная почта занята!", false)
    }

}