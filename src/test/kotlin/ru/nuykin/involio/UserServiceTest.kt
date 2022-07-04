package ru.nuykin.involio

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader
import ru.nuykin.involio.model.MyUser
import ru.nuykin.involio.repository.MyUserRepository
import ru.nuykin.involio.service.UserService
import java.util.*


@ExtendWith(SpringExtension::class)
@SpringBootTest
@ContextConfiguration(classes = [TestPortfolioConfig::class], loader = AnnotationConfigContextLoader::class)
class UserServiceTest {
    private val userRepository: MyUserRepository = Mockito.mock(MyUserRepository::class.java)

    private val userService: UserService = UserService()


    @Test
    fun createNewUser_whenSaveUser_shouldReturnUser() {
        val user = Mockito.mock(MyUser::class.java)
        Mockito.`when`(userRepository!!.save(user)).thenReturn(user)
        val created: MyUser = userService!!.createNewUser(user)
        assertThat(created.login).isSameAs(user.login)
        verify(userRepository).save(user)
    }

    @Test
    fun listAllUsers_shouldReturnAllUsers() {
        val users: MutableList<MyUser> = ArrayList()
        users.add(Mockito.mock(MyUser::class.java))
        given(userRepository!!.findAll()).willReturn(users)
        val expected: List<MyUser> = userService!!.listAllUsers()
        assertEquals(expected, users)
        verify(userRepository)!!.findAll()
    }

    @Test
    fun deleteUser_whenGivenId_shouldDeleteUser_ifFound() {
        val user = Mockito.mock(MyUser::class.java)
        `when`(userRepository!!.findById(user.login!!)).thenReturn(Optional.of(user))
        userService!!.deleteUser(user.login!!)
        verify(userRepository)!!.deleteById(user.login!!)
    }

    @Test
    fun deleteUser_should_throw_exception_when_user_doesnt_exist() {
        val user = Mockito.mock(MyUser::class.java)
        given(userRepository!!.findById("some")).willReturn(Optional.ofNullable(null))
        var isWasExcept = false
        try {
            userService!!.deleteUser(user.login!!)
        }
        catch (e: UsernameNotFoundException){
            isWasExcept = true
        }
        assertEquals(true, isWasExcept, "Не было исключения")
    }

    @Test
    fun updateUser_whenGivenId_shouldUpdateUser_ifFound() {
        val user = MyUser("", "1", "", "")
        val newUser = MyUser("", "2", "", "")
        given(userRepository!!.findById(user.login!!)).willReturn(Optional.of(user))
        userService!!.updateUser(user.login!!, newUser)
        verify(userRepository)!!.save(newUser)
        verify(userRepository)!!.findById(user.login!!)
    }

    @Test
    fun updateUser_should_throw_exception_when_user_doesnt_exist_in_update() {
        val user = Mockito.mock(MyUser::class.java)
        val newUser = Mockito.mock(MyUser::class.java)
        given(userRepository!!.findById("3")).willReturn(Optional.ofNullable(null))
        var isWasExcept = false
        try {
            userService!!.updateUser(user.login!!, newUser)
        }
        catch (e: UsernameNotFoundException){
            isWasExcept = true
        }
        assertEquals(true, isWasExcept, "Не было исключения")
    }

    @Test
    fun listUser_whenGivenId_shouldReturnUser_ifFound() {
        val user = Mockito.mock(MyUser::class.java)
        `when`(userRepository!!.findById(user.login!!)).thenReturn(Optional.of(user))
        val expected: MyUser = userService!!.listUser(user.login!!)
        assertThat(expected).isSameAs(user)
        verify(userRepository)!!.findById(user.login!!)
    }

    @Test
    fun listUser_should_throw_exception_when_user_doesnt_exist() {
        val user = Mockito.mock(MyUser::class.java)
        given(userRepository!!.findById("3")).willReturn(Optional.ofNullable(null))
        var isWasExcept = false
        try {
            userService!!.listUser(user.login!!)
        }
        catch (e: UsernameNotFoundException){
            isWasExcept = true
        }
        assertEquals(true, isWasExcept, "Не было исключения")
    }
}