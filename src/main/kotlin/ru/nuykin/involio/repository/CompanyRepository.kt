package ru.nuykin.involio.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.Company


@Repository
interface CompanyRepository : CrudRepository<Company, String> {
    fun findByNameCompanyContainingIgnoreCase(nameCompany: String, page: Pageable): List<Company>
}