package ru.nuykin.involio.dto

data class StockDto(
    var ticker: String,
    var exchange: ExchangeDto,
    var stock_company: CompanyDto,
    var trading_currency: CurrencyDto,
    var is_available_to_unqualified_investors: Boolean,
)
