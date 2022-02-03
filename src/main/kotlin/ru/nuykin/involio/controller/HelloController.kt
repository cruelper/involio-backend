package ru.nuykin.involio.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.Query
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

import org.springframework.web.bind.annotation.RestController
import ru.nuykin.involio.dto.MyUserDto
import ru.nuykin.involio.model.Branch
import ru.nuykin.involio.model.MyUser
import ru.nuykin.involio.repository.BranchRepository
import ru.nuykin.involio.repository.MyUserRepository


@RestController
@Api(description = "Контроллер проверки связи с сервером и проверки работоспособности")
class HelloController {

    @Autowired
    private val dao: MyUserRepository? = null

    @Autowired
    private val dao2: BranchRepository? = null

    @GetMapping("/")
    @ApiOperation("Проверка соединения с сервером")
    fun isConnect(): Boolean {
        return true
    }

    @GetMapping("/user")
    @ApiOperation("Для проверки разграничений прав для юзера")
    fun user(): String {
        return "User"
    }

    @GetMapping("/admin")
    @ApiOperation("Для проверки разграничений прав для админа")
    fun admin(): String {
        return "Admin"
    }

//    @GetMapping("/getall")
//    @ApiOperation("(Убрать) Получить всех юзеров")
//    fun getall(): List<MyUserDto> = dao!!.findAll().toList().map { MyUserDto(it.email!!, it.login!!, it.password!!) }

    @GetMapping("/add-data")
    fun add_data(): List<Branch?> =
        dao2!!.findAll().toList()

}