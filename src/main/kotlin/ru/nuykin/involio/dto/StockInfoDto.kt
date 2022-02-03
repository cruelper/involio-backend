package ru.nuykin.involio.dto

import java.sql.Date

data class StockInfoDto(
    var nameExchangeSource: String,
    var nameOtherExchanges: List<String>,
    var currentPrice: Double,
    var currencySign: Char,

    //Вкладка "В портфелях"
    var inPortfolio: List<ItemInPortfolio>,

    //Вкладка "Динамика цены"
    var dayInterval: List<Double>,
    var weekInterval: List<Double>,
    var monthInterval: List<Double>,
    var yearInterval: List<Double>,
    var fullInterval: List<Double>,

    //Вкладка "О компании"
    var nameCompany: String,
    var nameCountry: String,
    var descriptionCompany: String,
    var branch: List<String>,
    var sector: List<String>,

    //Вкладка "Сделки"
    var transactions: List<Transaction>,

    //Вкладка "Дивиденды"
    var dividends: List<DividendsDto>,
)

data class Transaction(
    var portfolioName: String,
    var date: Date,
    var count: Int,
    var price: Double,
    var saleOrPurchase: String,
)

data class ItemInPortfolio(
    var namePortfolio: String,
    var partOfPortfolio: Double,
    var currencySign: Char,
    var countInPortfolio: Int,
    var averagePurchasePrice: Double,
    var purchases: List<Triple<Date, Int, Double>>,
)

data class SearchedElement(
    var nameCompany: String,
    var ticker: String,
    var idExchange: Int,
    var nameExchange: String,
)
