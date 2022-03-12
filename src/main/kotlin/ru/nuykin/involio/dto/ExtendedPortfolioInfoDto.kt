package ru.nuykin.involio.dto

import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import java.util.Date

data class ExtendedPortfolioInfoDto (
    var id: Int,
    var name: String,
    var nameBroker: String,
    var nameTypeOfBrokerAccount: String,

    var dateOfCreation: Date,

    // вкладка портфель
    var signs: Map<String, Char>,
    var curPriceInRubl: Double,
    var curPriceInUsd: Double,
    var curPriceInEuro: Double,

    var rubInterval: ValuesInInterval,
    var usdInterval: ValuesInInterval,
    var euroInterval: ValuesInInterval,

    // вкладка анализ
    // стринга это название элемента, дабл - его часть в портфеле
    var assets: List<Pair<String, Double>>,
    var companies: List<Pair<String, Double>>,
    var branches: List<Pair<String, Double>>,
    var sectors: List<Pair<String, Double>>,
    var currencies: List<Pair<String, Double>>,

    // вкладка расчет налогов
    var tax: String,
)

data class ValuesInInterval(
    var monthInterval: List<Double>,
    var monthData: BasicValues,

    var yearInterval: List<Double>,
    var yearData: BasicValues,

    var allInterval: List<Double>,
    var allData: BasicValues,
)

data class BasicValues(
    var income: Double,
    var dividends: Double,
    var brokerCommission: Double,
    var depositsAndWithdrawalsDiff: Double,
)

data class IndicesInterval(
    // вкладка сравнение
    // данные для графика портфеля уже есть
    // берем только данные за все время
    var indicesIntervalsInRuble: Map<String, List<Double>>,
    var indicesIntervalsInUsd: Map<String, List<Double>>,
    var indicesIntervalsInEuro: Map<String, List<Double>>,
)
