package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.nuykin.involio.repository.CurrentPortfolioCompositionRepository
import ru.nuykin.involio.repository.InvestmentPortfolioRepository
import ru.nuykin.involio.repository.PortfoliosValueHistoryRepository

@Service
class BasicPortfoliosService{
    @Autowired
    private val CurrentPortfolioCompositionDao: CurrentPortfolioCompositionRepository? = null
    @Autowired
    private val InvestmentPortfolioDao: InvestmentPortfolioRepository? = null
    @Autowired
    private val PortfoliosValueHistoryDao: PortfoliosValueHistoryRepository? = null


}