package ru.nuykin.involio.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.model.InvestmentPortfolio
import ru.nuykin.involio.service.StockService
import ru.nuykin.involio.util.*


@RestController
@Api(description = "Контроллер для получения информации по акциям")
class StockController {

    @Autowired
    private val stockService: StockService? = null

    @GetMapping("/user/stock/{ticker}/{idExchange}")
    @ApiOperation("Получение полной информации по конкретной акции")
    fun getStockInfo(
        @PathVariable ticker: String,
        @PathVariable idExchange: Int,
        @RequestHeader("Authorization") token: String
    ): StockInfoDto =
        stockService!!.getStockInfo(ticker, idExchange, token)

    @GetMapping("/user/stock/{ticker}/{idExchange}/price")
    @ApiOperation("Получение цены конкретной акции")
    fun getCurrentStockPrice(@PathVariable ticker: String, @PathVariable idExchange: Int): Double =
        getPrice(stockService!!.getStock(ticker, idExchange).tiker_on_yahoo_api!!)

    @GetMapping("/user/stock/{ticker}/{idExchange}/interval-price/{interval}")
    @ApiOperation("Получение интервала цен конкретной акции")
    fun getIntervalStockPrice(@PathVariable ticker: String,
                              @PathVariable idExchange: Int,
                              @PathVariable interval: String
    ): List<Double> =
        getInterval(stockService!!.getStock(ticker, idExchange).tiker_on_yahoo_api!!, interval)

    @GetMapping("/user/stock/{ticker}/{idExchange}/transactions")
    @ApiOperation("Получение всех транзакций по конкретной акции")
    fun getStockTransactions(@PathVariable ticker: String,
                             @PathVariable idExchange: Int,
                             @RequestHeader("Authorization") token: String
    ): List<Transaction> {
        val transactions: ArrayList<Transaction> = ArrayList<Transaction>()
        for (i in stockService!!.getPortfolios(token).map { stockService.getTransactions(it, stockService.getStock(ticker, idExchange)) })
            transactions.addAll(i)
        return transactions
    }

    @GetMapping("/user/stock/{ticker}/{idExchange}/stock-in-portfolio-info")
    @ApiOperation("Получение информации о наличии данной акции в портфеле")
    fun getStockInPortfolioInfo(@PathVariable ticker: String,
                                @PathVariable idExchange: Int,
                                @RequestHeader("Authorization") token: String
    ): List<ItemInPortfolio> =
        stockService!!.getStockOfOneCompanyInPortfolio(stockService.getStock(ticker, idExchange), stockService.getPortfolios(token))

    @GetMapping("/user/search/stock/{searchString}/{page}")
    @ApiOperation("Поиск акций")
    fun searchStock(@PathVariable searchString: String, @PathVariable page: Int): List<SearchedElement> =
        stockService!!.searchStock(searchString, page)

}