package ru.nuykin.involio.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.dto.DividendsDto
import ru.nuykin.involio.model.Company
import ru.nuykin.involio.model.Dividends
import ru.nuykin.involio.model.DividendsId


@Repository
interface DividendsRepository : CrudRepository<Dividends, DividendsId> {
    @Query("select dividend from Dividends dividend where dividend.dividends_company = :company")
    fun findAllDividendsByIsin(company: Company): List<Dividends>?
}