package ru.nuykin.involio.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.service.InvestmentPortfolioService


@RestController
@Api(description = "Контроллер для работы с портфелями")
class PortfoliosController {

    @Autowired
    private val portfolioService: InvestmentPortfolioService? = null

    @GetMapping("/user/portfolios")
    @ApiOperation("Получение портфелей пользователя")
    fun getListPortfolio(@RequestHeader("Authorization") token: String): List<PortfolioDto> =
        portfolioService!!.getListPortfolio(token)

    // Получить полную инфу по портфелю (для вкладки подробно). График изменения цены, диверсификация и тд
    @GetMapping("/user/portfolios/{id}/extended")
    @ApiOperation("Получение дополнительной информации о портфеле")
    fun getExtendedPortfolioInfo(@PathVariable id: Int, @RequestHeader("Authorization") token: String): ExtendedPortfolioInfoDto? =
        portfolioService!!.getExtendedPortfolioInfo(id, token)

    @GetMapping("/user/portfolios/{id}/extended/comparison")
    @ApiOperation("Получение данных для построения графиков изменения индексов")
    fun getComparisonWithIndicesInfo(@RequestBody listIdIndices: List<String>, @PathVariable id: Int): IndicesInterval =
        portfolioService!!.getComparisonWithIndicesInfo(id, listIdIndices)


    @GetMapping("/user/portfolios/{id}/group-by-company={YesOrNo}")
    @ApiOperation("Получение основной информации по конкретному портфелю")
    fun getBasicPortfolioInfo(@PathVariable id: Int,
                              @RequestHeader("Authorization") token: String,
                              @PathVariable YesOrNo: String
    ): BasicPortfolioInfoDto? =
        portfolioService!!.getBasicPortfolioInfo(id, token,  YesOrNo == "yes")

    @PostMapping("user/portfolios/add-stock")
    @ApiOperation("Добавление акции в портфель")
    fun addStockToPortfolio(
        @RequestHeader("Authorization") token: String,
        @RequestBody compositionOfPortfolio: CompositionOfPortfolioDto
    ): Boolean =
        portfolioService!!.addStockToPortfolio(token, compositionOfPortfolio)

    @PostMapping("/user/portfolios")
    @ApiOperation("Создание портфеля")
    fun createPortfolio(@RequestBody portfolio: PortfolioDto, @RequestHeader("Authorization") token: String): Boolean =
        portfolioService!!.createPortfolio(portfolio, token)

    @DeleteMapping("user/portfolios/{id}")
    @ApiOperation("Удаление портфеля")
    fun deletePortfolio(@PathVariable id: Int, @RequestHeader("Authorization") token: String): HttpStatus =
        portfolioService!!.deletePortfolio(id, token)
}