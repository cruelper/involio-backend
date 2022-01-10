package ru.nuykin.involio.dto

import ru.nuykin.involio.model.Exchange

data class BrokerDto (
    val id: Int,
    val name: String,
    val listExchange: List<ExchangeDto>,
)