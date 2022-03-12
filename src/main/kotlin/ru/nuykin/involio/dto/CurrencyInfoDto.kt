package ru.nuykin.involio.dto

import java.sql.Date

data class CurrencyInfoDto(
    var currentPriceInRuble: Double,
    var signOfCurrency: String,

    //Вкладка "В портфелях"
    var inPortfolio: List<ItemInPortfolio>,

    //Вкладка "Динамика цены"
    var dayInterval: List<Pair<Long, Double>>,
    var weekInterval: List<Pair<Long, Double>>,
    var monthInterval: List<Pair<Long, Double>>,
    var yearInterval: List<Pair<Long, Double>>,
    var fullInterval: List<Pair<Long, Double>>,
)

