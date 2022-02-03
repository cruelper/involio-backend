package ru.nuykin.involio.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.model.Currency
import ru.nuykin.involio.service.CurrencyService
import ru.nuykin.involio.service.StockService
import ru.nuykin.involio.util.getInterval
import ru.nuykin.involio.util.getPrice

@RestController
@Api(description = "Контроллер для получения информации по валютам")
class CurrencyController {

    @Autowired
    private val currencyService: CurrencyService? = null

    @GetMapping("/user/currency/{id}")
    @ApiOperation("Получение полной информации по конкретной валюте")
    fun getCurrencyInfo(
        @PathVariable id: String,
        @RequestHeader("Authorization") token: String
    ): CurrencyInfoDto =
        currencyService!!.getCurrencyInfo(id, token)

    @GetMapping("/user/currency/{id}/price")
    @ApiOperation("Получение цены конкретной валюты")
    fun getCurrentCurrencyPrice(@PathVariable id: String): Double =
        getPrice(currencyService!!.getCurrency(id).id_on_yahoo_api!!)

    @GetMapping("/user/currency/{id}/interval-price/{interval}")
    @ApiOperation("Получение интервала цен конкретной валюты")
    fun getIntervalStockPrice(@PathVariable id: String,
                              @PathVariable interval: String
    ): List<Double> =
        getInterval(currencyService!!.getCurrency(id).id_on_yahoo_api!!, interval)

    @GetMapping("/user/currency/{id}/stock-in-portfolio-info")
    @ApiOperation("Получение информации о наличии данной валюты в портфеле")
    fun getStockInPortfolioInfo(@PathVariable id: String,
                                @RequestHeader("Authorization") token: String
    ): List<ItemInPortfolio> =
        currencyService!!.getCurrencyInPortfolioInfo(currencyService.getCurrency(id), currencyService.getPortfolios(token))

    @GetMapping("/user/search/currency/{searchString}")
    @ApiOperation("Поиск валюты")
    fun searchStock(@PathVariable searchString: String): List<CurrencyDto> =
        currencyService!!.searchCurrency(searchString)

}