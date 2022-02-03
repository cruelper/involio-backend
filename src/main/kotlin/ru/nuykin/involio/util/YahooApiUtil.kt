package ru.nuykin.involio.util

import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.data.repository.findByIdOrNull
import ru.nuykin.involio.dto.ChangePrice
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset
import java.sql.Date

fun readAll(rd: Reader): String? {
    val sb = StringBuilder()
    var cp: Int
    while (rd.read().also { cp = it } != -1) {
        sb.append(cp.toChar())
    }
    return sb.toString()
}

fun readJsonFromUrl(url: String?): JSONObject {
    val file: InputStream = URL(url).openStream()
    return try {
        val rd = BufferedReader(InputStreamReader(file, Charset.forName("UTF-8")))
        val jsonText: String = readAll(rd)!!
        JSONObject(jsonText)
    } finally {
        file.close()
    }
}

fun getIntervalInJsonArray(tickerOnYahooApi: String, period1: String, interval: String): JSONArray{
    val json: JSONObject = readJsonFromUrl("https://query1.finance.yahoo.com/v8/finance/chart/$tickerOnYahooApi?symbol=$tickerOnYahooApi&period1=$period1&period2=9999999999&interval=$interval")
    return json
        .getJSONObject("chart")
        .getJSONArray("result")
        .getJSONObject(0)
        .getJSONObject("indicators")
        .getJSONArray("quote")
        .getJSONObject(0)
        .getJSONArray("low")
}

fun getPrice(tickerOnYahoo: String): Double{
    val json: JSONObject = readJsonFromUrl("https://query1.finance.yahoo.com/v10/finance/quoteSummary/$tickerOnYahoo?modules=price")

    return json
        .getJSONObject("quoteSummary")
        .getJSONArray("result")
        .getJSONObject(0)
        .getJSONObject("price")
        .getJSONObject("regularMarketPrice")
        .get("raw")
        .toString()
        .toDouble()

}

fun getInterval(tickerOnYahoo: String, interval: String): List<Double>{
    var stepInLoop: Int =
        when(interval){
            "week" -> 12
            else -> 1
        }
    val stepTime: String =
        when(interval){
            "day" -> "5m"
            "week" -> "5m"
            else -> "1d"
        }

    var unixTime: Long = System.currentTimeMillis() / 1000L
    val backStep: Long =
        when(interval){
            "day" -> 86400L
            "week" -> 86400L*7
            "month" -> 86400L*30
            "year" -> 86400L*365
            "full" -> unixTime
            else -> 0
        }
    unixTime-=backStep

    val jsonListOfPrice: JSONArray = getIntervalInJsonArray(tickerOnYahoo, unixTime.toString(), stepTime)

    val listOfPrice: ArrayList<Double> = ArrayList<Double>()

    if (jsonListOfPrice.length() / stepInLoop > 250) stepInLoop *= (jsonListOfPrice.length() / 250)

    for (i in 0 until jsonListOfPrice.length() step stepInLoop){
        listOfPrice.add(jsonListOfPrice[i].toString().toDouble())
    }

    return listOfPrice.toList()
}

//endDate = curDate
fun currencyRateChangeInRuble(tickerOnYahooApi: String, startDate: Long): Double{
    val interval: String = "1d"
    val period1: String = startDate.toString()

    val jsonListOfPrice: JSONArray = getIntervalInJsonArray(tickerOnYahooApi, period1, interval)

    return jsonListOfPrice[jsonListOfPrice.length() - 1].toString().toDouble() /
            jsonListOfPrice[0].toString().toDouble()
}

fun getChangeIndicesOrIndicators(tickerOnYahooApi: String, startDate: Long): ChangePrice{
    val dayInterval: List<Double> = getInterval(tickerOnYahooApi, "day")
    val dayChange: Double = dayInterval[dayInterval.size - 1] / dayInterval[0]

    val yearInterval: List<Double> = getInterval(tickerOnYahooApi, "year")
    val yearChange: Double = yearInterval[yearInterval.size - 1] / yearInterval[0]

    val interval: String = "1d"
    val period1: String = startDate.toString()

    val jsonListOfPrice: JSONArray = getIntervalInJsonArray(tickerOnYahooApi, period1, interval)

    val arbitraryChange: Double =
        jsonListOfPrice[jsonListOfPrice.length() - 1].toString().toDouble() / jsonListOfPrice[0].toString().toDouble()

    return ChangePrice(dayChange, yearChange, arbitraryChange)
}

