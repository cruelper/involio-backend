package ru.nuykin.involio.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.Stock
import ru.nuykin.involio.model.StockId


@Repository
interface StockRepository : CrudRepository<Stock, StockId> {
    fun findByTickerContainingIgnoreCase(ticker: String, page: Pageable): List<Stock>
}