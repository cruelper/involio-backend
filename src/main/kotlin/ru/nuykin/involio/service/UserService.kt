package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.nuykin.involio.model.MyUser
import ru.nuykin.involio.repository.MyUserRepository
import java.util.function.Supplier


@Service
class UserService {
    @Autowired
    var repository: MyUserRepository? = null

    fun createNewUser(user: MyUser): MyUser {
        return repository!!.save(user)
    }

    fun deleteUser(login: String) {
        repository!!.findByIdOrNull(login) ?: throw UsernameNotFoundException("Unknown user: $login")
        repository!!.deleteById(login)
    }

    fun listUser(login: String): MyUser {
        return repository!!.findByIdOrNull(login) ?: throw UsernameNotFoundException("Unknown user: $login")
    }

    fun listAllUsers(): List<MyUser> {
        return repository!!.findAll() as List<MyUser>
    }

    fun updateUser(login: String, user: MyUser): MyUser? {
        repository!!.findByIdOrNull(login) ?: throw UsernameNotFoundException("Unknown user: $login")
        user.login = login
        return repository!!.save(user)
    }
}
