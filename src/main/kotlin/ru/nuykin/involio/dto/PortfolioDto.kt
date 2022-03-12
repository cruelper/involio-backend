package ru.nuykin.involio.dto

import java.util.Date

data class PortfolioDto(
    val id: Int,
    val name: String,
    val idBroker: Int,
    val idTypeBrokerAccount: Int,
    val dataOfCreation: Date,
)
