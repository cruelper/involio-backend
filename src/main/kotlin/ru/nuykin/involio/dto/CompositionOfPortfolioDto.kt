package ru.nuykin.involio.dto

import ru.nuykin.involio.model.InvestmentPortfolio

data class CompositionOfPortfolioDto(
    val idPortfolio: Int,
    val ticker: String,
    var idExchange: Int,
    val date: Long,
    val count: Int,
    val priceOfUnit: Double
)
