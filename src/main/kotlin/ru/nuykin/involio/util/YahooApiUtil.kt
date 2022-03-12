package ru.nuykin.involio.util

import org.springframework.boot.configurationprocessor.json.JSONObject
import ru.nuykin.involio.dto.ChangePrice
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset
import java.util.*

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

fun getIntervalInListDatePrice(tickerOnYahooApi: String, period1: String, interval: String): List<Pair<Long, Double>>{
    val json: JSONObject = readJsonFromUrl("https://query1.finance.yahoo.com/v8/finance/chart/$tickerOnYahooApi?symbol=$tickerOnYahooApi&period1=$period1&period2=9999999999&interval=$interval")
    val indicators = json
        .getJSONObject("chart")
        .getJSONArray("result")
        .getJSONObject(0)

    return try {
        val prices = indicators
            .getJSONObject("indicators")
            .getJSONArray("quote")
            .getJSONObject(0)
            .getJSONArray("close")
        val dates = indicators
            .getJSONArray("timestamp")

        val merge: MutableList<Pair<Long, Double>> = mutableListOf()
        for (i in 0 until dates.length()) merge.add(Pair(dates.getLong(i), prices.getDouble(i)))
        return merge
    }catch (e: Exception){
        val lastPrice = indicators
            .getJSONObject("meta")
            .getDouble("regularMarketPrice")
        val unixTime = System.currentTimeMillis() / 1000L
        return listOf(Pair(unixTime, lastPrice), Pair(unixTime, lastPrice))
    }

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

fun getInterval(tickerOnYahoo: String, interval: String): List<Pair<Long, Double>>{
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

    val calendar = GregorianCalendar()
    when(interval){
        "day" -> calendar.add(Calendar.DAY_OF_YEAR, -1)
        "week" -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
        "month" -> calendar.add(Calendar.MONTH, -1)
        "year" -> calendar.add(Calendar.YEAR, -1)
    }
    val unixTime: Long = calendar.time.time / 1000L

    val jsonListOfPrice: List<Pair<Long, Double>> = getIntervalInListDatePrice(tickerOnYahoo, unixTime.toString(), stepTime)

    val listOfPrice: ArrayList<Pair<Long, Double>> = ArrayList<Pair<Long, Double>>()

    if (jsonListOfPrice.size / stepInLoop > 250) stepInLoop *= (jsonListOfPrice.size / 250)

    for (i in jsonListOfPrice.indices step stepInLoop)
        if (jsonListOfPrice[i].toString() != "null") listOfPrice.add(jsonListOfPrice[i])

    return listOfPrice.toList()
}

//endDate = curDate
fun currencyRateChangeInRuble(tickerOnYahooApi: String, startDate: Long): Double{
    val interval: String = "1d"
    val period1: String = startDate.toString()

    val jsonListOfPrice: List<Pair<Long, Double>> = getIntervalInListDatePrice(tickerOnYahooApi, period1, interval)

    return jsonListOfPrice[jsonListOfPrice.size - 1].second /
            jsonListOfPrice[0].second
}

fun getChangeIndicesOrIndicators(tickerOnYahooApi: String, startDate: Long): ChangePrice{
    val dayInterval: List<Pair<Long, Double>> = getInterval(tickerOnYahooApi, "day")
    val dayChange: Double = dayInterval[dayInterval.size - 1].second / dayInterval[0].second

    val yearInterval:List<Pair<Long, Double>> = getInterval(tickerOnYahooApi, "year")
    val yearChange: Double = yearInterval[yearInterval.size - 1].second / yearInterval[0].second

    val interval: String = "1d"
    val period1: String = startDate.toString()

    val jsonListOfPrice: List<Pair<Long, Double>> = getIntervalInListDatePrice(tickerOnYahooApi, period1, interval)
    val arbitraryChange: Double =
        jsonListOfPrice[jsonListOfPrice.size - 1].second / jsonListOfPrice[0].second

    return ChangePrice(dayChange, yearChange, arbitraryChange)
}

