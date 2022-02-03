package ru.nuykin.involio.dto

import java.sql.Date

data class CurrencyInfoDto(
    var currentPriceInRuble: Double,
    var signOfCurrency: Char,

    //Вкладка "В портфелях"
    var inPortfolio: List<ItemInPortfolio>,

    //Вкладка "Динамика цены"
    var dayInterval: List<Double>,
    var weekInterval: List<Double>,
    var monthInterval: List<Double>,
    var yearInterval: List<Double>,
    var fullInterval: List<Double>,
)

