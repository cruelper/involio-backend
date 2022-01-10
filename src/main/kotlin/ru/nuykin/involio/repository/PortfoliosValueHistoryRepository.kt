package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.PortfoliosValueHistory
import ru.nuykin.involio.model.PortfoliosValueHistoryId


@Repository
interface PortfoliosValueHistoryRepository :
    CrudRepository<PortfoliosValueHistory, PortfoliosValueHistoryId> {
}