package ru.nuykin.involio.dto

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item
import java.io.IOException


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

//@JsonSerialize(using = ChangePriceToStringConverter::class)
data class ChangePrice(
    val priceChangeOnDay: Double,
    val priceChangeOnYear: Double,
    val priceChangeOnAllTime: Double,
)

//@JsonSerialize(using = PortfolioPriceToStringConverter::class)
data class PortfolioPrice(
    val currentPrice: Double,
    val changePrice: ChangePrice,
    val currencySign: Char,
)

//@JsonSerialize(using = StockInPortfolioToStringConverter::class)
data class StockInPortfolio(
    var name: String,
    var ticker: String,
    var count: Int,
    var currentUnitPrice: Double,
    var partOfPortfolio: Double,
    var changePrice: ChangePrice,
)


class StockInPortfolioToStringConverter @JvmOverloads constructor(t: Class<StockInPortfolio?>? = null) :
    StdSerializer<StockInPortfolio>(t) {
    override fun serialize(p0: StockInPortfolio?, p1: JsonGenerator?, p2: SerializerProvider?) {
        p1?.writeStartObject()
        p1?.writeStringField("name", p0?.name)
        p1?.writeStringField("ticker", p0?.ticker)
        p1?.writeNumberField("count", p0?.count!!)
        p1?.writeStringField("currentUnitPrice", String.format("%.3f", p0?.currentUnitPrice!!))
        p1?.writeStringField("partOfPortfolio", String.format("%.2f", p0?.currentUnitPrice!!))
        p1?.writeObjectField("changePrice", p0?.changePrice)
        p1?.writeEndObject()
    }
}

class ChangePriceToStringConverter @JvmOverloads constructor(t: Class<ChangePrice?>? = null) :
    StdSerializer<ChangePrice>(t) {
    override fun serialize(p0: ChangePrice?, p1: JsonGenerator?, p2: SerializerProvider?) {
        p1?.writeStartObject()
        p1?.writeStringField("priceChangeOnDay", String.format("%.2f", p0?.priceChangeOnDay!!))
        p1?.writeStringField("priceChangeOnYear", String.format("%.2f", p0?.priceChangeOnYear!!))
        p1?.writeStringField("priceChangeOnAllTime", String.format("%.2f", p0?.priceChangeOnAllTime!!))
        p1?.writeEndObject()
    }
}

class PortfolioPriceToStringConverter @JvmOverloads constructor(t: Class<PortfolioPrice?>? = null) :
    StdSerializer<PortfolioPrice>(t) {
    override fun serialize(p0: PortfolioPrice?, p1: JsonGenerator?, p2: SerializerProvider?) {
        p1?.writeStartObject()
        p1?.writeStringField("currentPrice", String.format("%.2f", p0?.currentPrice!!))
        p1?.writeObjectField("changePrice", p0?.changePrice)
        p1?.writeStringField("currencySign", p0?.currencySign!!.toString())
        p1?.writeEndObject()
    }
}
