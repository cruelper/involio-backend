package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.CompositionOfPortfolioDto
import ru.nuykin.involio.dto.SectorDto
import ru.nuykin.involio.model.CurrentPortfolioComposition
import ru.nuykin.involio.model.InvestmentPortfolio
import ru.nuykin.involio.model.Sector
import ru.nuykin.involio.repository.CurrentPortfolioCompositionRepository
import ru.nuykin.involio.repository.SectorRepository
import java.util.*

@Service
class CurrentPortfolioCompositionService {
    @Autowired
    private val compositionDao: CurrentPortfolioCompositionRepository? = null

    fun addStockToComposition(portfolio: InvestmentPortfolio, compositionOfPortfolio: CompositionOfPortfolioDto){
        val curPortfolioComposition = CurrentPortfolioComposition(
            portfolioToComposition = portfolio,
            date = Date(compositionOfPortfolio.date),
            ticker = compositionOfPortfolio.ticker,
            idExchange = compositionOfPortfolio.idExchange,
            count = compositionOfPortfolio.count,
            priceOfUnit = compositionOfPortfolio.priceOfUnit
        )

        val equalCompos: CurrentPortfolioComposition? = portfolio.composition_of_portfolio!!.find { it == curPortfolioComposition }
        if (equalCompos == null) compositionDao!!.save(curPortfolioComposition)
        else {
            curPortfolioComposition.count = curPortfolioComposition.count!! + equalCompos.count!!
            compositionDao!!.save(curPortfolioComposition)
        }
    }

    fun addCurrencyToComposition(portfolio: InvestmentPortfolio, compositionOfPortfolio: CompositionOfPortfolioDto){
        compositionOfPortfolio.idExchange = -1
        addStockToComposition(portfolio, compositionOfPortfolio)
    }
}
