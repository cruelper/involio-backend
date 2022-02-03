package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.InvestmentPortfolio
import ru.nuykin.involio.model.Stock
import ru.nuykin.involio.model.StockTransaction
import ru.nuykin.involio.model.StockTransactionId


@Repository
interface StockTransactionRepository : CrudRepository<StockTransaction, StockTransactionId> {
    fun getAllByInvestmentPortfolioInStockTransactionAndStockInTransaction(
        portfolio: InvestmentPortfolio,
        stock: Stock
    ): List<StockTransaction>?
}