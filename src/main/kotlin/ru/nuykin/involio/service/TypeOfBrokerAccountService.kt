package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.TypeOfBrokerAccountDto
import ru.nuykin.involio.model.TypeOfBrokerAccount
import ru.nuykin.involio.repository.TypeOfBrokerAccountRepository

@Service
class TypeOfBrokerAccountService{
    @Autowired
    private val typeOfBrokerAccountDao: TypeOfBrokerAccountRepository? = null

    fun getAllTypeOfBrokerAccount(): List<TypeOfBrokerAccountDto> =
        typeOfBrokerAccountDao!!.findAll().toList()
            .map { TypeOfBrokerAccountDto(it.id_type_of_broker_account!!, it.name_type_of_broker_account!!) }

    fun getTypeOfBrokerAccountById(id: Int): TypeOfBrokerAccountDto? {
        val typeOfBrokerAccount: TypeOfBrokerAccount? = typeOfBrokerAccountDao!!.findByIdOrNull(id)
        return if(typeOfBrokerAccount == null) null else
            TypeOfBrokerAccountDto(typeOfBrokerAccount.id_type_of_broker_account!!, typeOfBrokerAccount.name_type_of_broker_account!!)
    }

    fun addTypeOfBrokerAccount(typeOfBrokerAccountDto: TypeOfBrokerAccountDto){
        val newTypeOfBrokerAccount: TypeOfBrokerAccount = TypeOfBrokerAccount(name_type_of_broker_account = typeOfBrokerAccountDto.name)
        typeOfBrokerAccountDao!!.save(newTypeOfBrokerAccount)
    }

    fun updateTypeOfBrokerAccount(typeOfBrokerAccountDto: TypeOfBrokerAccountDto){
        val typeOfBrokerAccount: TypeOfBrokerAccount? = typeOfBrokerAccountDao!!.findByIdOrNull(typeOfBrokerAccountDto.id)
        if(typeOfBrokerAccount != null){
            typeOfBrokerAccount.name_type_of_broker_account = typeOfBrokerAccountDto.name
            typeOfBrokerAccountDao.save(typeOfBrokerAccount)
        }
    }

    fun deleteTypeOfBrokerAccountById(id: Int){
        typeOfBrokerAccountDao!!.deleteById(id)
    }

}
