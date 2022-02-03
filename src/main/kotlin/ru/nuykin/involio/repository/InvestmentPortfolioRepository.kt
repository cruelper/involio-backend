package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.Branch
import ru.nuykin.involio.model.InvestmentPortfolio
import ru.nuykin.involio.model.MyUser


@Repository
interface InvestmentPortfolioRepository : CrudRepository<InvestmentPortfolio, Int> {
    fun findAllByOwner(owner: MyUser?): List<InvestmentPortfolio>?
}