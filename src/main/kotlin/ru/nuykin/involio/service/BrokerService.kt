package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.nuykin.involio.dto.BrokerDto
import ru.nuykin.involio.dto.ExchangeDto
import ru.nuykin.involio.model.Broker
import ru.nuykin.involio.model.Exchange
import ru.nuykin.involio.repository.BrokerRepository
import ru.nuykin.involio.repository.ExchangeRepository

@Service
@Transactional
class BrokerService{
    @Autowired
    private val brokerDao: BrokerRepository? = null

    fun getAllBroker(): List<BrokerDto> =
        brokerDao!!.findAll().toList().map { curBroker ->
            BrokerDto(
                curBroker.id_broker!!,
                curBroker.name_broker!!,
                curBroker.brokers_exchange!!.toList().map { ExchangeDto(it.id_exchange!!, it.name_exchange!!) }
            ) }

    fun getBrokerById(id: Int): BrokerDto? {
        val broker: Broker? = brokerDao!!.findByIdOrNull(id)
        return if(broker == null) null else
            BrokerDto(
                broker.id_broker!!,
                broker.name_broker!!,
                broker.brokers_exchange!!.toList().map { ExchangeDto(it.id_exchange!!, it.name_exchange!!) }
            )
    }

    fun addBroker(name: String, listExchange: List<Exchange>){
        val newBroker: Broker = Broker(name, listExchange)
        brokerDao!!.save(newBroker)
    }

    fun updateBroker(id: Int, name: String, listExchange: List<Exchange>){
        val broker: Broker? = brokerDao!!.findByIdOrNull(id)
        if(broker != null){
            broker.name_broker = name
            broker.brokers_exchange = listExchange
            brokerDao.save(broker)
        }
    }

    fun deleteBrokerById(id: Int){
        brokerDao!!.deleteById(id)
    }
}