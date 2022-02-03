package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.ExchangeDto
import ru.nuykin.involio.model.Exchange
import ru.nuykin.involio.repository.ExchangeRepository

@Service
class ExchangeService{
    @Autowired
    private val exchangeDao: ExchangeRepository? = null

    fun getAllExchange(): List<ExchangeDto> =
        exchangeDao!!.findAll().toList().map { ExchangeDto(it.id_exchange, it.name_exchange!!) }

    fun getDtoExchangeById(id: Int): ExchangeDto? {
        val exchange: Exchange? = exchangeDao!!.findByIdOrNull(id)
        return if(exchange == null) null else ExchangeDto(exchange.id_exchange, exchange.name_exchange!!)
    }

    fun getExchangeById(id: Int): Exchange? {
        return exchangeDao!!.findByIdOrNull(id)
    }

    fun addExchange(exchangeDto: ExchangeDto){
        val newExchange: Exchange = Exchange(name = exchangeDto.name)
        exchangeDao!!.save(newExchange)
    }

    fun updateExchange(exchangeDto: ExchangeDto){
        val exchange = exchangeDao!!.findByIdOrNull(exchangeDto.id)
        if(exchange != null){
            exchange.name_exchange = exchangeDto.name
            exchangeDao.save(exchange)
        }
    }

    fun deleteExchangeById(id: Int){
        exchangeDao!!.deleteById(id)
    }

}