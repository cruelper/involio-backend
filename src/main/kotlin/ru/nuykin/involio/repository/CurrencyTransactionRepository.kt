package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.Currency
import ru.nuykin.involio.model.CurrencyTransaction
import ru.nuykin.involio.model.CurrencyTransactionId
import ru.nuykin.involio.model.InvestmentPortfolio


@Repository
interface CurrencyTransactionRepository : CrudRepository<CurrencyTransaction, CurrencyTransactionId> {
    fun findByInvestmentPortfolioInCurrencyTransactionAndCurrencyInTransaction(portfolio: InvestmentPortfolio, currency: Currency):
            List<CurrencyTransaction>?
}