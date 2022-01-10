package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.CurrencyTransaction
import ru.nuykin.involio.model.CurrencyTransactionId


@Repository
interface CurrencyTransactionRepository : CrudRepository<CurrencyTransaction, CurrencyTransactionId> {
}