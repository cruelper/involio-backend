package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.Exchange


@Repository
interface ExchangeRepository : CrudRepository<Exchange, Int> {
}