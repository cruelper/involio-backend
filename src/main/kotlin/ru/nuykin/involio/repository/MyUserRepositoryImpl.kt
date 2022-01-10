package ru.nuykin.involio.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.MyUser


@Repository
interface MyUserRepository : CrudRepository<MyUser, String> {
    fun findByLogin(login: String?): MyUser?
}