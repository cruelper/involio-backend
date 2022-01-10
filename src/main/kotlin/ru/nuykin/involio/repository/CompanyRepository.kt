package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.Company


@Repository
interface CompanyRepository : CrudRepository<Company, String> {
}