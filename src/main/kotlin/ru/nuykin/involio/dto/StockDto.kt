package ru.nuykin.involio.dto

data class StockDto(
    var ticker: String,
    var idExchange: Int,
    var CompanyISIN: String,
    var idCurrency: String,
    var isAvailableToUnqualifiedInvestors: Boolean,
    var tickerOnYahooApi: String,
)
