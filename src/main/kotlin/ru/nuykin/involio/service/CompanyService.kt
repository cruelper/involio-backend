package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.model.*
import ru.nuykin.involio.repository.CompanyRepository

@Service
class CompanyService{
    @Autowired
    private val companyDao: CompanyRepository? = null

    private fun getDto(company: Company): CompanyDto =
        CompanyDto(
            isin = company.isin!!,
            nameCompany = company.nameCompany!!,
            description = company.description!!,
            currency = CurrencyDto(company.id_currency!!.id_currency!!, company.id_currency!!.name_currency!!),
            country = CountryDto(company.country!!.id_country!!, company.country!!.name_country!!),
            branch = company.branch!!.toList().map { BranchDto(it.idBranch!!, it.nameBranch!!) },
            sector = company.sector!!.toList().map { SectorDto(it.id_sector!!, it.name_sector!!) },
        )

    fun getAllCompany(): List<CompanyDto> =
        companyDao!!.findAll().toList().map { getDto(it) }

    fun getDtoCompanyById(isin: String): CompanyDto? {
        val company: Company? = companyDao!!.findByIdOrNull(isin)
        return if(company == null) null else getDto(company)
    }

    fun getCompanyById(isin: String): Company? =
        companyDao!!.findByIdOrNull(isin)

    fun addOrUpdateCompany(company: Company) {
        companyDao!!.save(company)
    }

    fun deleteCompanyById(isin: String){
        companyDao!!.deleteById(isin)
    }

}
