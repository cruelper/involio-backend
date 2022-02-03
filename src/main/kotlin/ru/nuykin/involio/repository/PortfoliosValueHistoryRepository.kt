package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.PortfoliosValueHistory
import ru.nuykin.involio.model.PortfoliosValueHistoryId
import java.sql.Date


@Repository
interface PortfoliosValueHistoryRepository :
    CrudRepository<PortfoliosValueHistory, PortfoliosValueHistoryId> {
        fun findByDatePortfoliosValue(date_portfolios_value: Date): PortfoliosValueHistory?
}