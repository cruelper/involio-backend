package ru.nuykin.involio.dto

import ru.nuykin.involio.model.*

data class CompanyDto(
    var isin: String,
    var nameCompany: String,
    var description: String,
    var currency: CurrencyDto,
    var country: CountryDto,
    var branch: Collection<BranchDto>,
    var sector: Collection<SectorDto>,
)
