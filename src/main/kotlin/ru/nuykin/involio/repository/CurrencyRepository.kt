package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.Currency


@Repository
interface CurrencyRepository : CrudRepository<Currency, String> {
    fun findByIdCurrencyContainingIgnoreCase(id: String): List<Currency>

}