package ru.nuykin.involio.dto

data class BasicPortfolioInfoDto(
    var id: Int,
    var name: String,
    var nameBroker: String,
    var nameTypeOfBrokerAccount: String,

    var InRuble: PortfolioPrice,
    var InUSD: PortfolioPrice,
    var InEuro: PortfolioPrice,

    var ChangeSP500InRuble: ChangePrice,
    var ChangeSP500InUSD: ChangePrice,
    var ChangeSP500InEURO: ChangePrice,

    var ChangeIMOEXInRuble: ChangePrice,
    var ChangeIMOEXInUSD: ChangePrice,
    var ChangeIMOEXInEURO: ChangePrice,

    var stocksInPortfolio: List<StockInPortfolio>,
)

data class ChangePrice(
    val priceChangeOnDay: Double,
    val priceChangeOnYear: Double,
    val priceChangeOnAllTime: Double,
)

data class PortfolioPrice(
    val currentPrice: Double,
    val changePrice: ChangePrice,
    val currencySign: Char,
)

data class StockInPortfolio(
    var name: String,
    var ticker: String,
    var count: Int,
    var currentUnitPrice: Double,
    var partOfPortfolio: Double,
    var changePrice: ChangePrice,
)
