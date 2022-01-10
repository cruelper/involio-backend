package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.CurrencyDto
import ru.nuykin.involio.model.Currency
import ru.nuykin.involio.repository.CurrencyRepository

@Service
class CurrencyService{
    @Autowired
    private val currencyDao: CurrencyRepository? = null

    fun getAllCurrency(): List<CurrencyDto> =
        currencyDao!!.findAll().toList().map { CurrencyDto(it.id_currency!!, it.name_currency!!) }

    fun getCurrencyById(id: String): Currency? =
        currencyDao!!.findByIdOrNull(id)

    fun getDtoCurrencyById(id: String): CurrencyDto? {
        val currency: Currency? = currencyDao!!.findByIdOrNull(id)
        return if(currency == null) null else CurrencyDto(currency.id_currency!!, currency.name_currency!!)
    }

    fun addCurrency(currencyDto: CurrencyDto){
        val newCurrency: Currency = Currency(id = currencyDto.id, name = currencyDto.name)
        currencyDao!!.save(newCurrency)
    }

    fun updateCurrency(currencyDto: CurrencyDto){
        val currency = currencyDao!!.findByIdOrNull(currencyDto.id)
        if(currency != null){
            currency.name_currency = currencyDto.name
            currencyDao.save(currency)
        }
    }

    fun deleteCurrencyById(id: String){
        currencyDao!!.deleteById(id)
    }
}
