package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.DividendsDto
import ru.nuykin.involio.model.Company
import ru.nuykin.involio.model.Dividends
import ru.nuykin.involio.model.DividendsId
import ru.nuykin.involio.repository.DividendsRepository
import java.sql.Date

@Service
class DividendsService{
    @Autowired
    private val dividendsDao: DividendsRepository? = null

    fun getDividendsByCompanyIsin(company: Company): List<DividendsDto> {
        val dividends: List<Dividends>? = dividendsDao!!.findAllDividendsByIsin(company)
        return if(dividends == null) emptyList() else dividends.map { DividendsDto(it.date_dividends!!, it.absolut_size!!, it.relative_size!!) }
    }

    fun addDividendsToCompany(company: Company, dividendsDto: DividendsDto){
        val newDividends: Dividends = Dividends(company, dividendsDto.date_dividends, dividendsDto.absolut_size, dividendsDto.relative_size)
        dividendsDao!!.save(newDividends)
    }

    fun deleteCompanyDividends(date: Date, company: Company){
        val dividendsId: DividendsId = DividendsId(company, date)
        dividendsDao!!.deleteById(dividendsId)
    }
}
